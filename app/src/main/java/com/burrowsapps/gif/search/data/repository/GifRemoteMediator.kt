package com.burrowsapps.gif.search.data.repository

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class GifRemoteMediator(
  private val queryKey: String,
  private val repository: GifRepository,
  private val database: AppDatabase,
  private val dispatcher: CoroutineDispatcher,
) : RemoteMediator<Int, GifImageInfo>() {
  companion object {
    // Run orphan cleanup at most once every CLEANUP_INTERVAL refreshes (count-based throttling)...
    private const val CLEANUP_INTERVAL = 5

    // ...or once enough time has passed since the last cleanup (time-based throttling).
    private val MIN_CLEANUP_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5)

    // Cached results older than this are considered stale and trigger a refresh when the app reopens.
    private val CACHE_TTL_MS = TimeUnit.HOURS.toMillis(1)
  }

  // Throttling state is per-mediator: each instance handles exactly one queryKey, and Paging invokes
  // load() serially, so plain fields are safe. This replaces the previous companion-object
  // ConcurrentHashMaps, which were keyed by every distinct search and never evicted, growing
  // unbounded for the whole process lifetime.
  private var refreshCount = 0

  // Initialized to "now" (not 0L) so a freshly constructed mediator is treated as "just cleaned":
  // cleanup is gated by the count/time throttle instead of firing on the very first refresh.
  private var lastCleanupTime = System.currentTimeMillis()

  /**
   * Whether orphan cleanup should run for this load. Counts every refresh and runs cleanup on every
   * CLEANUP_INTERVAL-th refresh, or whenever at least MIN_CLEANUP_INTERVAL_MS has elapsed since the
   * last successful cleanup. The counter advances on every refresh; the timestamp only advances when
   * cleanup actually completes (see recordCleanupCompleted).
   */
  private fun shouldRunCleanup(): Boolean {
    refreshCount++
    val shouldRunByCount = refreshCount % CLEANUP_INTERVAL == 0
    val shouldRunByTime = (System.currentTimeMillis() - lastCleanupTime) >= MIN_CLEANUP_INTERVAL_MS
    return shouldRunByCount || shouldRunByTime
  }

  /** Records that cleanup has successfully completed, resetting the time-based throttle. */
  private fun recordCleanupCompleted() {
    lastCleanupTime = System.currentTimeMillis()
  }

  override suspend fun initialize(): InitializeAction {
    // Skip the initial network refresh only when cached rows exist AND are still fresh. Without a
    // TTL, the trending feed (and every search) would stay frozen at its first fetch forever; with
    // it, a stale cache triggers a refresh on app open while a fresh cache still avoids flicker.
    val hasData = database.queryResultDao().nextPositionForQuery(queryKey) > 0
    val lastUpdated = database.remoteKeysDao().remoteKeys(queryKey)?.lastUpdated ?: 0L
    val isFresh = System.currentTimeMillis() - lastUpdated < CACHE_TTL_MS
    return if (hasData && isFresh) {
      InitializeAction.SKIP_INITIAL_REFRESH
    } else {
      InitializeAction.LAUNCH_INITIAL_REFRESH
    }
  }

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, GifImageInfo>,
  ): MediatorResult {
    return try {
      val remoteKeysDao = database.remoteKeysDao()
      val gifDao = database.gifDao()
      val queryResultsDao = database.queryResultDao()

      // Determine the cursor position for the API call
      val currentKey =
        when (loadType) {
          LoadType.REFRESH -> {
            // REFRESH loads from the beginning with fresh data
            null
          }

          LoadType.PREPEND -> {
            // Tenor API doesn't support backwards pagination
            return MediatorResult.Success(endOfPaginationReached = true)
          }

          LoadType.APPEND -> {
            // Get the next cursor from our stored remote keys
            val remoteKey = remoteKeysDao.remoteKeys(queryKey)
            if (remoteKey?.nextKey == null) {
              // No next key means we've reached the end
              return MediatorResult.Success(endOfPaginationReached = true)
            }
            remoteKey.nextKey
          }
        }

      Timber.d("GifRemoteMediator: query='$queryKey', loadType=$loadType, cursor=$currentKey")

      // Fetch data from network on IO dispatcher
      val result: NetworkResult<GifResponseDto> =
        withContext(dispatcher) {
          if (queryKey.isBlank()) {
            repository.getTrendingResults(currentKey)
          } else {
            repository.getSearchResults(queryKey, currentKey)
          }
        }

      when (result) {
        is NetworkResult.Error -> {
          Timber.e("GifRemoteMediator: Network error - ${result.message}")
          MediatorResult.Error(Exception(result.message))
        }

        is NetworkResult.Loading -> {
          // This shouldn't happen in our flow, but handle it defensively
          MediatorResult.Error(IllegalStateException("Unexpected Loading state"))
        }

        is NetworkResult.Empty -> {
          Timber.d("GifRemoteMediator: Empty result for query='$queryKey'")
          MediatorResult.Success(endOfPaginationReached = true)
        }

        is NetworkResult.Success -> {
          val response = result.data
          val items = buildGifList(response)

          // Check if we've reached the end of pagination
          // Tenor API returns the same cursor when no more data is available,
          // or an empty/blank string, or fewer items than requested
          val nextCursor =
            response
              ?.next
              ?.takeIf { it.isNotBlank() && it != currentKey }
          // Mutable so an APPEND page that adds no new rows (all duplicates) can also end pagination.
          var reachedEnd = items.isEmpty() || nextCursor == null
          val fetchedAt = System.currentTimeMillis()

          Timber.d(
            "GifRemoteMediator: Loaded ${items.size} items, nextCursor=$nextCursor, " +
              "endOfPagination=$reachedEnd",
          )

          // Save everything to database in a single transaction for consistency
          database.withTransaction {
            if (items.isNotEmpty()) {
              // Step 1: Upsert GIF entities FIRST (deduplicated by primary key: tinyGifUrl)
              // This ensures GIFs are available before we reference them
              val gifEntities =
                items.map { info ->
                  GifEntity(
                    tinyGifUrl = info.tinyGifUrl,
                    tinyGifPreviewUrl = info.tinyGifPreviewUrl,
                    gifUrl = info.gifUrl,
                    gifPreviewUrl = info.gifPreviewUrl,
                  )
                }
              gifDao.upsertAll(gifEntities)

              // Step 2: Calculate starting position for this batch
              val startPos =
                if (loadType == LoadType.APPEND) {
                  // Append: continue from last position
                  queryResultsDao.nextPositionForQuery(queryKey)
                } else {
                  // Refresh: start from 0
                  0L
                }

              // Step 3: Create query result mappings with stable, sequential positions
              val mappings =
                items.mapIndexed { index, item ->
                  QueryResultEntity(
                    searchKey = queryKey,
                    gifId = item.tinyGifUrl, // Foreign key to GifEntity
                    position = startPos + index,
                  )
                }

              // Step 4: For REFRESH, clear old data THEN insert new data atomically
              // This minimizes the time window where the database appears empty
              if (loadType == LoadType.REFRESH) {
                queryResultsDao.clearQuery(queryKey)
                remoteKeysDao.clearQuery(queryKey)
              }

              // insertAll uses IGNORE; already-seen (searchKey, gifId) rows are dropped (rowId -1).
              // If an APPEND page is entirely duplicates, nothing new lands — end pagination so
              // Paging doesn't keep fetching fresh cursors that never grow the list.
              val insertedCount = queryResultsDao.insertAll(mappings).count { it != -1L }
              if (loadType == LoadType.APPEND && insertedCount == 0) {
                reachedEnd = true
              }

              Timber.d("GifRemoteMediator: Saved $insertedCount/${items.size} items from position $startPos")
            } else if (loadType == LoadType.REFRESH) {
              // If no items, still clear old data
              queryResultsDao.clearQuery(queryKey)
              remoteKeysDao.clearQuery(queryKey)
            }

            // Step 5: Store the next cursor (null once we've reached the end) plus a fetch timestamp
            // that initialize() uses for its staleness/TTL check.
            remoteKeysDao.upsert(
              RemoteKeysEntity(
                searchKey = queryKey,
                nextKey = if (reachedEnd) null else nextCursor,
                lastUpdated = fetchedAt,
              ),
            )
          }

          // Cleanup orphaned GIFs outside the transaction, on the IO dispatcher (never the main
          // thread). It runs inline, so it slightly delays this load()'s completion (and the refresh
          // spinner); the committed rows are already pushed to the UI by Room before this runs.
          if (loadType == LoadType.REFRESH && shouldRunCleanup()) {
            withContext(dispatcher) {
              try {
                val cleanupStartTime = System.currentTimeMillis()
                val orphanedCount = gifDao.deleteOrphanedGifs()

                // Only record successful cleanup to ensure accurate throttling
                // If cleanup fails, we'll retry on the next interval
                recordCleanupCompleted()

                if (orphanedCount > 0) {
                  Timber.d(
                    "GifRemoteMediator: Cleaned up $orphanedCount orphaned GIF entities " +
                      "for query='$queryKey' (took ${System.currentTimeMillis() - cleanupStartTime}ms)",
                  )
                }
              } catch (e: Exception) {
                // Log but don't fail the load if cleanup fails
                // Don't record cleanup time so it will retry on next interval
                Timber.e(
                  e,
                  "GifRemoteMediator: Failed to clean up orphaned GIFs for query='$queryKey'",
                )
              }
            }
          }

          MediatorResult.Success(endOfPaginationReached = reachedEnd)
        }
      }
    } catch (e: Exception) {
      Timber.e(e, "GifRemoteMediator: Exception during load")
      MediatorResult.Error(e)
    }
  }

  private fun buildGifList(response: GifResponseDto?): List<GifImageInfo> =
    response
      ?.results
      ?.mapNotNull { result ->
        // Use mapNotNull to safely skip any items with missing data
        val media = result.media.firstOrNull() ?: return@mapNotNull null

        // Defensive null checks even though DTOs have default values
        // This protects against malformed JSON or future API changes
        val gif = media.gif
        val tinyGif = media.tinyGif

        // Skip items with missing URLs (defensive programming)
        if (tinyGif.url.isBlank()) return@mapNotNull null

        GifImageInfo(
          gifUrl = gif.url,
          gifPreviewUrl = gif.preview,
          tinyGifUrl = tinyGif.url,
          tinyGifPreviewUrl = tinyGif.preview,
        )
      }.orEmpty()
}

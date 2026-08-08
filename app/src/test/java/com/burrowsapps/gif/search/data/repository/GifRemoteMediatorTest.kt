package com.burrowsapps.gif.search.data.repository

import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.DataDto
import com.burrowsapps.gif.search.data.api.model.FileDto
import com.burrowsapps.gif.search.data.api.model.GifDto
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.MediaDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.api.model.ResultDto
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class GifRemoteMediatorTest {
  private lateinit var db: AppDatabase
  private val repository = mock<GifRepository>()
  private val dispatcher = UnconfinedTestDispatcher()

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(
          ApplicationProvider.getApplicationContext(),
          AppDatabase::class.java,
        ).allowMainThreadQueries()
        .build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  private fun media(url: String) = MediaDto(gif = GifDto(url = url), jpg = GifDto(url = "$url.jpg"))

  private fun result(
    gifUrl: String,
    tinyGifUrl: String,
  ) = ResultDto(file = FileDto(md = media(gifUrl), sm = media(tinyGifUrl)))

  private fun response(
    items: Int,
    prefix: String,
    currentPage: Int = 1,
    hasNext: Boolean = true,
  ): GifResponseDto {
    val results =
      (1..items).map { i ->
        result(
          gifUrl = "https://ex.com/${prefix}g$i.gif",
          tinyGifUrl = "https://ex.com/${prefix}t$i.gif",
        )
      }
    return GifResponseDto(
      result = true,
      data = DataDto(results = results, currentPage = currentPage, perPage = items, hasNext = hasNext),
    )
  }

  private fun emptyState(): PagingState<Int, GifImageInfo> =
    PagingState(
      pages = emptyList(),
      anchorPosition = null,
      config = PagingConfig(pageSize = 45),
      leadingPlaceholderCount = 0,
    )

  @Test
  fun refresh_trending_storesItemsAndKeys() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(
        NetworkResult.Success(
          response(2, "a"),
        ),
      )

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      val list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(2)
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("2")
    }

  @Test
  fun refresh_withExistingData_clearsAndReloads() =
    runTest(dispatcher) {
      // First load with old data
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "old")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Verify old data is there
      var list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(2)
      assertThat(list[0].tinyGifUrl).contains("old")

      // REFRESH should clear old data and load fresh from beginning
      whenever(repository.getTrendingResults(null))
        .thenReturn(NetworkResult.Success(response(3, "new")))

      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      // Should have only new data (old data was cleared)
      list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(3)
      assertThat(list[0].tinyGifUrl).contains("new")
      assertThat(list[1].tinyGifUrl).contains("new")
      assertThat(list[2].tinyGifUrl).contains("new")
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("2")
    }

  @Test
  fun append_trending_appendsAndUpdatesKey() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))
      whenever(repository.getTrendingResults("2"))
        .thenReturn(NetworkResult.Success(response(2, "b", currentPage = 2)))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())
      val result = mediator.load(LoadType.APPEND, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      val list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(4)
      assertThat(list.first().tinyGifUrl).contains("a")
      assertThat(list.last().tinyGifUrl).contains("b")
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("3")
    }

  @Test
  fun append_withNoNextKey_returnsEndOfPagination() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Set next key to null to simulate end of pagination
      db.remoteKeysDao().upsert(
        com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity(
          searchKey = "",
          nextKey = null,
        ),
      )

      val result = mediator.load(LoadType.APPEND, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

  @Test
  fun prepend_returnsEndOfPagination() =
    runTest(dispatcher) {
      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      val result = mediator.load(LoadType.PREPEND, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

  @Test
  fun error_returnsError() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(
        NetworkResult.Error(
          message = "boom",
        ),
      )
      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Error::class.java)
    }

  @Test
  fun empty_returnsEndOfPagination() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Empty())
      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

  @Test
  fun search_query_storesItemsWithCorrectKey() =
    runTest(dispatcher) {
      whenever(repository.getSearchResults("cats", null))
        .thenReturn(NetworkResult.Success(response(2, "cat")))

      val mediator =
        GifRemoteMediator(
          queryKey = "cats",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      val list = db.queryResultDao().allForQuery("cats")
      assertThat(list).hasSize(2)
      assertThat(list.first().tinyGifUrl).contains("cat")
      assertThat(db.remoteKeysDao().remoteKeys("cats")?.nextKey).isEqualTo("2")
    }

  @Test
  fun buildGifList_skipsItemsWithMissingData() =
    runTest(dispatcher) {
      // Create response with one item missing its file variants (all URLs blank)
      val validResult = result(gifUrl = "https://ex.com/g1.gif", tinyGifUrl = "https://ex.com/t1.gif")
      val invalidResult = ResultDto()

      val response =
        GifResponseDto(
          result = true,
          data = DataDto(results = listOf(validResult, invalidResult), hasNext = true),
        )
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Success(response))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Should only have 1 item (invalid one skipped)
      val list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(1)
    }

  @Test
  fun initialize_withNoData_launchesInitialRefresh() =
    runTest(dispatcher) {
      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )

      val action = mediator.initialize()
      assertThat(action).isEqualTo(androidx.paging.RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

  @Test
  fun initialize_withExistingData_skipsInitialRefresh() =
    runTest(dispatcher) {
      // Pre-populate database with data
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )

      // Load some data first
      mediator.load(LoadType.REFRESH, emptyState())

      // Now initialize should skip refresh
      val action = mediator.initialize()
      assertThat(action).isEqualTo(androidx.paging.RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

  @Test
  fun refresh_cleansUpOrphanedGifs() =
    runTest(dispatcher) {
      // Load "cats" query with 2 GIFs
      whenever(repository.getSearchResults("cats", null))
        .thenReturn(NetworkResult.Success(response(2, "cat")))

      val catMediator =
        GifRemoteMediator(
          queryKey = "cats",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      catMediator.load(LoadType.REFRESH, emptyState())

      // Verify 2 GIF entities exist
      val allGifsAfterCats = db.gifDao().allGifs()
      assertThat(allGifsAfterCats).hasSize(2)
      assertThat(allGifsAfterCats.all { it.tinyGifUrl.contains("cat") }).isTrue()

      // Now REFRESH the same "cats" query multiple times to trigger clean up
      // Cleanup runs every 5th refresh due to throttling
      whenever(repository.getSearchResults("cats", null))
        .thenReturn(NetworkResult.Success(response(2, "dog")))

      // Do 5 refreshes to hit the cleanup interval
      repeat(5) {
        catMediator.load(LoadType.REFRESH, emptyState())
      }

      // After 5 REFRESHes, cleanup should have run at least once
      // Old "cat" GIFs should be cleaned up, replaced with "dog" GIFs
      val allGifsAfterRefresh = db.gifDao().allGifs()
      assertThat(allGifsAfterRefresh).hasSize(2)
      assertThat(allGifsAfterRefresh.all { it.tinyGifUrl.contains("dog") }).isTrue()
      assertThat(allGifsAfterRefresh.none { it.tinyGifUrl.contains("cat") }).isTrue()

      // Verify "cats" query results point to new data
      val catsResults = db.queryResultDao().allForQuery("cats")
      assertThat(catsResults).hasSize(2)
      assertThat(catsResults.all { it.tinyGifUrl.contains("dog") }).isTrue()
    }

  @Test
  fun refresh_doesNotDeleteSharedGifs() =
    runTest(dispatcher) {
      // Create a GIF that will appear in both the first and second refresh
      val sharedResult =
        result(gifUrl = "https://ex.com/shared.gif", tinyGifUrl = "https://ex.com/sharedtiny.gif")
      val uniqueResult =
        result(gifUrl = "https://ex.com/unique.gif", tinyGifUrl = "https://ex.com/uniquetiny.gif")

      // First refresh: load 2 GIFs (shared + unique)
      val firstResponse =
        GifResponseDto(
          result = true,
          data = DataDto(results = listOf(sharedResult, uniqueResult), hasNext = true),
        )

      whenever(repository.getSearchResults("cats", null)).thenReturn(
        NetworkResult.Success(
          firstResponse,
        ),
      )

      val mediator =
        GifRemoteMediator(
          queryKey = "cats",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Verify 2 GIFs exist
      var allGifs = db.gifDao().allGifs()
      assertThat(allGifs).hasSize(2)

      // Second refresh: load only the shared GIF (unique GIF is gone from API results)
      val secondResponse =
        GifResponseDto(
          result = true,
          data = DataDto(results = listOf(sharedResult), hasNext = true),
        )
      whenever(repository.getSearchResults("cats", null)).thenReturn(
        NetworkResult.Success(
          secondResponse,
        ),
      )

      // Do 5 refreshes to trigger cleanup (runs every 5th refresh due to throttling)
      repeat(5) {
        mediator.load(LoadType.REFRESH, emptyState())
      }

      // After cleanup runs, unique.gif should be deleted (orphaned),
      // but sharedtiny.gif should remain (still referenced by "cats" query)
      allGifs = db.gifDao().allGifs()
      assertThat(allGifs).hasSize(1)
      assertThat(allGifs[0].tinyGifUrl).isEqualTo("https://ex.com/sharedtiny.gif")
    }

  @Test
  fun buildGifList_skipsItemsWithBlankUrls() =
    runTest(dispatcher) {
      val validResult = result(gifUrl = "https://ex.com/g1.gif", tinyGifUrl = "https://ex.com/t1.gif")
      // Blank tiny gif URL
      val invalidResult =
        ResultDto(file = FileDto(md = media("https://ex.com/g2.gif"), sm = MediaDto()))

      val response =
        GifResponseDto(
          result = true,
          data = DataDto(results = listOf(validResult, invalidResult), hasNext = true),
        )
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Success(response))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Should only have 1 item (item with blank URL skipped)
      val list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(1)
      assertThat(list[0].tinyGifUrl).isEqualTo("https://ex.com/t1.gif")
    }

  @Test
  fun refresh_withHasNextFalse_returnsEndOfPagination() =
    runTest(dispatcher) {
      // Last page: has_next=false means no further pages
      val response = response(2, "a", hasNext = false)
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Success(response))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      val result = mediator.load(LoadType.REFRESH, emptyState())

      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isNull()
    }

  @Test
  fun append_withEmptyItemsReturnsEndOfPagination() =
    runTest(dispatcher) {
      // First load succeeds
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Second load returns empty items (no more data)
      whenever(repository.getTrendingResults("2"))
        .thenReturn(NetworkResult.Success(response(0, "b", currentPage = 2))) // 0 items

      val result = mediator.load(LoadType.APPEND, emptyState())

      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

  @Test
  fun append_withAllDuplicateItems_returnsEndOfPagination() =
    runTest(dispatcher) {
      // First load caches 2 items with a next page.
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // APPEND returns the SAME items (all duplicates) but a fresh, different page.
      whenever(repository.getTrendingResults("2"))
        .thenReturn(NetworkResult.Success(response(2, "a", currentPage = 2)))

      val result = mediator.load(LoadType.APPEND, emptyState())

      // Nothing new was inserted, so pagination must end instead of spinning on fresh pages.
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
      assertThat(db.queryResultDao().allForQuery("")).hasSize(2)
    }

  @Test
  fun refresh_evictsStaleNeverRevisitedQueries_onFirstRefresh() =
    runTest(dispatcher) {
      // Seed a search the user never returned to, last fetched at the epoch (far past retention).
      db.gifDao().upsertAll(listOf(GifEntity("oldtiny", "p", "g", "gp")))
      db.queryResultDao().insertAll(listOf(QueryResultEntity("oldsearch", "oldtiny", 0)))
      db.remoteKeysDao().upsert(RemoteKeysEntity("oldsearch", nextKey = "1", lastUpdated = 0L))

      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      // A fresh mediator qualifies for cleanup on its very first refresh (time arm, since
      // lastCleanupTime starts at 0L), so a single app-open refresh is enough to evict.
      mediator.load(LoadType.REFRESH, emptyState())

      // The stale search and its now-orphaned GIF are gone; trending stays.
      assertThat(db.queryResultDao().allForQuery("oldsearch")).isEmpty()
      assertThat(db.remoteKeysDao().remoteKeys("oldsearch")).isNull()
      assertThat(db.gifDao().getById("oldtiny")).isNull()
      assertThat(db.queryResultDao().allForQuery("")).hasSize(2)
    }

  @Test
  fun refresh_throttlesEvictionUntilCleanupInterval() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      // First refresh runs cleanup and arms the throttle.
      mediator.load(LoadType.REFRESH, emptyState())

      // Seed stale data AFTER that cleanup. The time arm needs 5 real minutes, which a test run
      // never reaches, so from here only the count arm (every 5th refresh) can evict it.
      db.gifDao().upsertAll(listOf(GifEntity("oldtiny", "p", "g", "gp")))
      db.queryResultDao().insertAll(listOf(QueryResultEntity("oldsearch", "oldtiny", 0)))
      db.remoteKeysDao().upsert(RemoteKeysEntity("oldsearch", nextKey = "1", lastUpdated = 0L))

      // Refreshes 2-4: neither throttle arm fires, so the stale search must survive. This is what
      // pins the throttle itself — were cleanup to run on every refresh, these assertions fail.
      repeat(3) { mediator.load(LoadType.REFRESH, emptyState()) }
      assertThat(db.queryResultDao().allForQuery("oldsearch")).hasSize(1)
      assertThat(db.remoteKeysDao().remoteKeys("oldsearch")).isNotNull()

      // The 5th refresh hits the count arm and evicts.
      mediator.load(LoadType.REFRESH, emptyState())
      assertThat(db.queryResultDao().allForQuery("oldsearch")).isEmpty()
      assertThat(db.remoteKeysDao().remoteKeys("oldsearch")).isNull()
      assertThat(db.gifDao().getById("oldtiny")).isNull()
    }
}

package com.burrowsapps.gif.search.data.repository

import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.GifDto
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.MediaDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.api.model.ResultDto
import com.burrowsapps.gif.search.data.db.AppDatabase
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

  private fun response(
    items: Int,
    prefix: String,
    next: String,
  ): GifResponseDto {
    val results =
      (1..items).map { i ->
        val gif =
          GifDto(
            url = "https://ex.com/${prefix}g$i.gif",
            preview = "https://ex.com/${prefix}gp$i.gif",
          )
        val tiny =
          GifDto(
            url = "https://ex.com/${prefix}t$i.gif",
            preview = "https://ex.com/${prefix}tp$i.gif",
          )
        val media = MediaDto(gif = gif, tinyGif = tiny)
        ResultDto(media = listOf(media))
      }
    return GifResponseDto(results = results, next = next)
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
          response(
            2,
            "a",
            next = "10.0",
          ),
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
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("10.0")
    }

  @Test
  fun refresh_withExistingData_clearsAndReloads() =
    runTest(dispatcher) {
      // First load with old data
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "old", next = "10.0")))

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
        .thenReturn(NetworkResult.Success(response(3, "new", next = "5.0")))

      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      // Should have only new data (old data was cleared)
      list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(3)
      assertThat(list[0].tinyGifUrl).contains("new")
      assertThat(list[1].tinyGifUrl).contains("new")
      assertThat(list[2].tinyGifUrl).contains("new")
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("5.0")
    }

  @Test
  fun append_trending_appendsAndUpdatesKey() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))
      whenever(repository.getTrendingResults("10.0"))
        .thenReturn(NetworkResult.Success(response(2, "b", next = "20.0")))

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
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("20.0")
    }

  @Test
  fun append_withNoNextKey_returnsEndOfPagination() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))

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
        NetworkResult.Error<GifResponseDto>(
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
        .thenReturn(NetworkResult.Success(response(2, "cat", next = "5.0")))

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
      assertThat(db.remoteKeysDao().remoteKeys("cats")?.nextKey).isEqualTo("5.0")
    }

  @Test
  fun buildGifList_skipsItemsWithMissingData() =
    runTest(dispatcher) {
      // Create response with one item missing media
      val validGif = GifDto(url = "https://ex.com/g1.gif", preview = "https://ex.com/gp1.gif")
      val validTiny = GifDto(url = "https://ex.com/t1.gif", preview = "https://ex.com/tp1.gif")
      val validResult = ResultDto(media = listOf(MediaDto(gif = validGif, tinyGif = validTiny)))
      val invalidResult = ResultDto(media = emptyList())

      val response = GifResponseDto(results = listOf(validResult, invalidResult), next = "10.0")
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
        .thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))

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
        .thenReturn(NetworkResult.Success(response(2, "cat", next = "10.0")))

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

      // Now REFRESH the same "cats" query multiple times to trigger cleanup
      // Cleanup runs every 5th refresh due to throttling
      whenever(repository.getSearchResults("cats", null))
        .thenReturn(NetworkResult.Success(response(2, "dog", next = "5.0")))

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
      val sharedGif =
        GifDto(url = "https://ex.com/shared.gif", preview = "https://ex.com/sharedp.gif")
      val sharedTiny =
        GifDto(url = "https://ex.com/sharedtiny.gif", preview = "https://ex.com/sharedtinyp.gif")
      val uniqueGif =
        GifDto(url = "https://ex.com/unique.gif", preview = "https://ex.com/uniquep.gif")
      val uniqueTiny =
        GifDto(
          url = "https://ex.com/uniquetiny.gif",
          preview = "https://ex.com/uniquetinyp.gif",
        )

      // First refresh: load 2 GIFs (shared + unique)
      val firstResponse =
        GifResponseDto(
          results =
            listOf(
              ResultDto(media = listOf(MediaDto(gif = sharedGif, tinyGif = sharedTiny))),
              ResultDto(media = listOf(MediaDto(gif = uniqueGif, tinyGif = uniqueTiny))),
            ),
          next = "10.0",
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
          results =
            listOf(
              ResultDto(
                media =
                  listOf(
                    MediaDto(
                      gif = sharedGif,
                      tinyGif = sharedTiny,
                    ),
                  ),
              ),
            ),
          next = "5.0",
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
      val validGif = GifDto(url = "https://ex.com/g1.gif", preview = "https://ex.com/gp1.gif")
      val validTiny = GifDto(url = "https://ex.com/t1.gif", preview = "https://ex.com/tp1.gif")
      val blankGif = GifDto(url = "https://ex.com/g2.gif", preview = "https://ex.com/gp2.gif")
      val blankTiny = GifDto(url = "", preview = "https://ex.com/tp2.gif") // Blank URL

      val validResult = ResultDto(media = listOf(MediaDto(gif = validGif, tinyGif = validTiny)))
      val invalidResult = ResultDto(media = listOf(MediaDto(gif = blankGif, tinyGif = blankTiny)))

      val response = GifResponseDto(results = listOf(validResult, invalidResult), next = "10.0")
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
  fun append_withBlankNextCursor_returnsEndOfPagination() =
    runTest(dispatcher) {
      // First load returns blank next cursor
      val response = response(2, "a", next = "  ") // Blank/whitespace cursor
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
    }

  @Test
  fun append_withSameCursorAsCurrentReturnsEndOfPagination() =
    runTest(dispatcher) {
      // First load
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Second load returns same cursor (API signaling end of pagination)
      whenever(repository.getTrendingResults("10.0"))
        .thenReturn(NetworkResult.Success(response(2, "b", next = "10.0"))) // Same cursor!

      val result = mediator.load(LoadType.APPEND, emptyState())

      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

  @Test
  fun append_withEmptyItemsReturnsEndOfPagination() =
    runTest(dispatcher) {
      // First load succeeds
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))

      val mediator =
        GifRemoteMediator(
          queryKey = "",
          repository = repository,
          database = db,
          dispatcher = dispatcher,
        )
      mediator.load(LoadType.REFRESH, emptyState())

      // Second load returns empty items (no more data)
      whenever(repository.getTrendingResults("10.0"))
        .thenReturn(NetworkResult.Success(response(0, "b", next = "20.0"))) // 0 items

      val result = mediator.load(LoadType.APPEND, emptyState())

      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)
      assertThat((result as androidx.paging.RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }
}

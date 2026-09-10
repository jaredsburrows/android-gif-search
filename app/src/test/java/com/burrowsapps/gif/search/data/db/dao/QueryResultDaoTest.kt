package com.burrowsapps.gif.search.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueryResultDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: QueryResultDao

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    dao = db.queryResultDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun insertAndQueryOrdering() =
    runBlocking {
      val gifs =
        listOf(
          GifEntity("tiny1", "preview1", "gif1", "gifPrev1"),
          GifEntity("tiny2", "preview2", "gif2", "gifPrev2"),
          GifEntity("tiny3", "preview3", "gif3", "gifPrev3"),
        )
      db.gifDao().upsertAll(gifs)

      val query = ""
      val results =
        listOf(
          QueryResultEntity(query, "tiny2", 0),
          QueryResultEntity(query, "tiny1", 1),
          QueryResultEntity(query, "tiny3", 2),
        )
      dao.insertAll(results)

      val list = dao.allForQuery(query)
      assertThat(list.map { it.tinyGifUrl })
        .containsExactly("tiny2", "tiny1", "tiny3")
        .inOrder()
    }

  @Test
  fun nextPositionForQuery_returnsMaxPlusOne() =
    runBlocking {
      val gifs =
        listOf(
          GifEntity("a", "ap", "ag", "agp"),
          GifEntity("b", "bp", "bg", "bgp"),
        )
      db.gifDao().upsertAll(gifs)

      val key = "cats"
      dao.insertAll(
        listOf(
          QueryResultEntity(key, "a", 0),
          QueryResultEntity(key, "b", 1),
        ),
      )

      val next = dao.nextPositionForQuery(key)
      assertThat(next).isEqualTo(2)
    }

  @Test
  fun clearQuery_removesOnlyMatchingKey() =
    runBlocking {
      val gifs =
        listOf(
          GifEntity("x", "xp", "xg", "xgp"),
          GifEntity("y", "yp", "yg", "ygp"),
        )
      db.gifDao().upsertAll(gifs)

      dao.insertAll(
        listOf(
          QueryResultEntity("key1", "x", 0),
          QueryResultEntity("key2", "y", 0),
        ),
      )

      dao.clearQuery("key1")
      val remain1 = dao.allForQuery("key1")
      val remain2 = dao.allForQuery("key2")
      assertThat(remain1).isEmpty()
      assertThat(remain2).hasSize(1)
    }

  @Test
  fun cascade_deleteFromGifs_removesMappings() =
    runBlocking {
      val gif = GifEntity("z", "zp", "zg", "zgp")
      db.gifDao().upsertAll(listOf(gif))
      dao.insertAll(listOf(QueryResultEntity("", "z", 0)))

      // Delete all gifs; should cascade and remove query_results rows
      db.gifDao().clearAll()
      val remain = dao.allForQuery("")
      assertThat(remain).isEmpty()
    }

  @Test
  fun clearStaleQueries_removesOnlyStaleNonActiveQueries() =
    runBlocking {
      db.gifDao().upsertAll(
        listOf(
          GifEntity("f", "fp", "fg", "fgp"),
          GifEntity("s", "sp", "sg", "sgp"),
          GifEntity("a", "ap", "ag", "agp"),
        ),
      )
      // Three searches: one fresh, one stale-but-active (the query loading now), one stale.
      dao.insertAll(
        listOf(
          QueryResultEntity("fresh", "f", 0),
          QueryResultEntity("active", "a", 0),
          QueryResultEntity("stale", "s", 0),
        ),
      )
      val now = 10_000_000L
      val cutoff = now - 1_000L
      db.remoteKeysDao().upsert(RemoteKeysEntity("fresh", nextKey = "1", lastUpdated = now))
      db.remoteKeysDao().upsert(RemoteKeysEntity("active", nextKey = "1", lastUpdated = 0L))
      db.remoteKeysDao().upsert(RemoteKeysEntity("stale", nextKey = "1", lastUpdated = 0L))

      val removed = dao.clearStaleQueries(cutoff = cutoff, exceptKey = "active")

      assertThat(removed).isEqualTo(1)
      assertThat(dao.allForQuery("stale")).isEmpty()
      // Fresh (recent) and active (excluded despite being stale) both survive.
      assertThat(dao.allForQuery("fresh")).hasSize(1)
      assertThat(dao.allForQuery("active")).hasSize(1)
      // The stale GIF is now orphaned and reclaimable; the others remain referenced.
      assertThat(db.gifDao().deleteOrphanedGifs()).isEqualTo(1)
      assertThat(db.gifDao().count()).isEqualTo(2)
    }

  @Test
  fun reinsertingExistingGif_keepsMappingsAcrossQueries() =
    runBlocking {
      val gif = GifEntity("shared", "p", "g", "gp")
      db.gifDao().upsertAll(listOf(gif))
      // The same GIF is referenced by two different searches.
      dao.insertAll(
        listOf(
          QueryResultEntity("", "shared", 0),
          QueryResultEntity("cats", "shared", 0),
        ),
      )

      // The GIF reappears (later page / another search) and is re-inserted. With IGNORE this is a
      // no-op; with REPLACE it would DELETE+INSERT the gifs row and cascade-delete both mappings.
      db.gifDao().upsertAll(listOf(gif.copy(gifUrl = "changed")))

      assertThat(dao.allForQuery("")).hasSize(1)
      assertThat(dao.allForQuery("cats")).hasSize(1)
      assertThat(db.gifDao().count()).isEqualTo(1)
    }

  // Regression coverage for the room3 migration: room3-compiler no longer special-cases
  // PagingSource DAO returns on its own. Support only exists because QueryResultDao carries
  // @DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class) at the @Dao (class) level.
  // If that annotation were missing, misplaced (e.g. on the function instead of the interface), or
  // silently ignored by the compiler, pagingSource() would either fail to compile or -- worse --
  // compile but return a broken/empty PagingSource at runtime, which is exactly the kind of failure
  // that would slip past code review and only surface as an empty results grid in production.
  @Test
  fun pagingSource_load_returnsInsertedItemsInOrder() =
    runBlocking {
      val gifs =
        listOf(
          GifEntity("tiny1", "preview1", "gif1", "gifPrev1"),
          GifEntity("tiny2", "preview2", "gif2", "gifPrev2"),
        )
      db.gifDao().upsertAll(gifs)

      val query = "cats"
      dao.insertAll(
        listOf(
          QueryResultEntity(query, "tiny1", 0),
          QueryResultEntity(query, "tiny2", 1),
        ),
      )

      val pagingSource = dao.pagingSource(query)
      val result =
        pagingSource.load(
          PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
      val page = result as PagingSource.LoadResult.Page<Int, GifImageInfo>
      assertThat(page.data.map { it.tinyGifUrl }).containsExactly("tiny1", "tiny2").inOrder()
    }

  // Edge case: a query with no cached rows should yield a valid, empty page rather than throwing
  // or returning null/Invalid -- callers (Paging3) rely on that contract to render an empty list.
  @Test
  fun pagingSource_load_withNoMatchingRows_returnsEmptyPage() =
    runBlocking {
      val pagingSource = dao.pagingSource("no-such-query")
      val result =
        pagingSource.load(
          PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
      val page = result as PagingSource.LoadResult.Page<Int, GifImageInfo>
      assertThat(page.data).isEmpty()
    }
}

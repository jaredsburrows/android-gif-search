package com.burrowsapps.gif.search.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
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
}

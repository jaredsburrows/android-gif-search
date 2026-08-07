package com.burrowsapps.gif.search.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeysDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: RemoteKeysDao

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    dao = db.remoteKeysDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun upsert_get_clear() =
    runBlocking {
      val query = ""
      assertThat(dao.remoteKeys(query)).isNull()
      dao.upsert(RemoteKeysEntity(searchKey = query, nextKey = "1.0"))
      assertThat(dao.remoteKeys(query)?.nextKey).isEqualTo("1.0")
      dao.upsert(RemoteKeysEntity(searchKey = query, nextKey = "2.0"))
      assertThat(dao.remoteKeys(query)?.nextKey).isEqualTo("2.0")
      dao.clearQuery(query)
      assertThat(dao.remoteKeys(query)).isNull()
    }

  @Test
  fun clearStale_removesOlderThanCutoffExceptActive() =
    runBlocking {
      val now = 10_000_000L
      val cutoff = now - 1_000L
      dao.upsert(RemoteKeysEntity("fresh", nextKey = "1", lastUpdated = now))
      dao.upsert(RemoteKeysEntity("active", nextKey = "1", lastUpdated = 0L))
      dao.upsert(RemoteKeysEntity("stale", nextKey = "1", lastUpdated = 0L))

      val removed = dao.clearStale(cutoff = cutoff, exceptKey = "active")

      assertThat(removed).isEqualTo(1)
      assertThat(dao.remoteKeys("stale")).isNull()
      // Recent key and the excluded active key both survive.
      assertThat(dao.remoteKeys("fresh")).isNotNull()
      assertThat(dao.remoteKeys("active")).isNotNull()
    }
}

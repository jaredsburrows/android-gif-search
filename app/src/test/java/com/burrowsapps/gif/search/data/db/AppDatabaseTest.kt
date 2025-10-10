package com.burrowsapps.gif.search.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
  private lateinit var db: AppDatabase

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun daoProviders_areAvailable() {
    assertThat(db.gifDao()).isNotNull()
    assertThat(db.queryResultDao()).isNotNull()
    assertThat(db.remoteKeysDao()).isNotNull()
  }

  @Test
  fun foreignKey_preventsOrphanMapping() =
    runBlocking {
      // No GIF with id "missing"
      var threw = false
      try {
        db.queryResultDao().insertAll(listOf(QueryResultEntity("k", "missing", 0)))
      } catch (e: Exception) {
        threw = e is SQLiteConstraintException || e.cause is SQLiteConstraintException
      }
      assertThat(threw).isTrue()
    }

  @Test
  fun foreignKey_cascadeOnGifDelete() =
    runBlocking {
      db.gifDao().upsertAll(listOf(GifEntity("g1", "p1", "u1", "pu1")))
      db.queryResultDao().insertAll(listOf(QueryResultEntity("k", "g1", 0)))

      db.gifDao().clearAll()
      val remain = db.queryResultDao().allForQuery("k")
      assertThat(remain).isEmpty()
    }
}

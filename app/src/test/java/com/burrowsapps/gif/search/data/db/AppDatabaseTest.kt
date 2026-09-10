package com.burrowsapps.gif.search.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.room3.Room
import androidx.room3.withWriteTransaction
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

  // Regression coverage for the room3 migration: androidx.room3 dropped withTransaction in favor
  // of withReadTransaction/withWriteTransaction. GifRemoteMediator's two write sites both switched
  // to withWriteTransaction; this verifies the replacement genuinely commits writes on success...
  @Test
  fun withWriteTransaction_commitsWritesOnSuccess() =
    runBlocking {
      db.withWriteTransaction {
        db.gifDao().upsertAll(listOf(GifEntity("g1", "p1", "u1", "pu1")))
        db.queryResultDao().insertAll(listOf(QueryResultEntity("k", "g1", 0)))
      }

      assertThat(db.gifDao().count()).isEqualTo(1)
      assertThat(db.queryResultDao().allForQuery("k")).hasSize(1)
    }

  // ...and, critically, that a read-then-write sequence inside one lambda is still atomic: a
  // failure partway through must roll back every write in that block, not just the one that threw.
  // This is the same shape as GifRemoteMediator's transactions (e.g. read nextPositionForQuery,
  // then write inserts) -- if withWriteTransaction silently narrowed to per-statement commits
  // instead of one real transaction, this test would catch it as a partial write surviving.
  @Test
  fun withWriteTransaction_rollsBackAllWritesOnFailure() =
    runBlocking {
      var threw = false
      try {
        db.withWriteTransaction {
          // Write #1
          db.gifDao().upsertAll(listOf(GifEntity("g1", "p1", "u1", "pu1")))
          // Read inside the same transaction, consistent with the write above
          val countAfterWrite = db.gifDao().count()
          assertThat(countAfterWrite).isEqualTo(1)
          // Write #2, then fail before the transaction can commit
          db.queryResultDao().insertAll(listOf(QueryResultEntity("k", "g1", 0)))
          throw IllegalStateException("boom")
        }
      } catch (e: IllegalStateException) {
        threw = true
      }

      assertThat(threw).isTrue()
      // Both writes -- not just the one nearest the throw -- must be rolled back.
      assertThat(db.gifDao().count()).isEqualTo(0)
      assertThat(db.queryResultDao().allForQuery("k")).isEmpty()
    }
}

package com.burrowsapps.gif.search.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: GifDao

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    dao = db.gifDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun upsert_and_clear_flow() =
    runBlocking {
      val entity = GifEntity("tiny1", "preview1", "gif1", "gifPrev1")
      val entity2 = GifEntity("tiny2", "preview2", "gif2", "gifPrev2")
      dao.upsertAll(listOf(entity, entity2))
      assertThat(dao.count()).isEqualTo(2)

      val updated = entity2.copy(gifPreviewUrl = "changed")
      dao.upsertAll(listOf(updated))
      assertThat(dao.getById("tiny2")?.gifPreviewUrl).isEqualTo("changed")

      dao.clearAll()
      assertThat(dao.count()).isEqualTo(0)
    }
}

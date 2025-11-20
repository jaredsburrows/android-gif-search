@file:OptIn(ExperimentalCoroutinesApi::class)

package com.burrowsapps.gif.search.ui.giflist

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.repository.GifRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class GifViewModelTest {
  private val testDispatcher = UnconfinedTestDispatcher()
  private val repository = mock<GifRepository>()

  private lateinit var sut: GifViewModel
  private lateinit var db: AppDatabase

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(
          ApplicationProvider.getApplicationContext(),
          AppDatabase::class.java,
        ).allowMainThreadQueries()
        .build()
    sut = GifViewModel(repository, db, testDispatcher)
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun searchText_initiallyEmpty() {
    assertThat(sut.searchText.value).isEmpty()
  }

  @Test
  fun onSearchTextChanged_updatesState() {
    sut.onSearchTextChanged("cats")
    assertThat(sut.searchText.value).isEqualTo("cats")
  }

  @Test
  fun onSearchTextChanged_withWhitespace_keepsOriginal() {
    sut.onSearchTextChanged("  dogs  ")
    // searchText stores the raw input
    assertThat(sut.searchText.value).isEqualTo("  dogs  ")
    // The normalization happens in the flow (trim, lowercase)
  }

  @Test
  fun onClearClick_resetsSearchText() {
    sut.onSearchTextChanged("dogs")
    sut.onClearClick()
    assertThat(sut.searchText.value).isEmpty()
  }

  @Test
  fun gifPagingData_isNotNull() {
    // Basic smoke test that the Flow is properly initialized
    assertThat(sut.gifPagingData).isNotNull()
  }
}

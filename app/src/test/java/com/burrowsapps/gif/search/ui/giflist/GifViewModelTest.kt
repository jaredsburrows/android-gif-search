@file:OptIn(ExperimentalCoroutinesApi::class)

package com.burrowsapps.gif.search.ui.giflist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.repository.GifRepository
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifViewModelTest {
  private val testDispatcher = UnconfinedTestDispatcher()
  private val repository = mock<GifRepository>()

  private lateinit var sut: GifViewModel

  @Before
  fun setUp() {
    sut = GifViewModel(repository, testDispatcher)
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
  fun onClearClick_resetsSearchText() {
    sut.onSearchTextChanged("dogs")
    sut.onClearClick()
    assertThat(sut.searchText.value).isEmpty()
  }
}

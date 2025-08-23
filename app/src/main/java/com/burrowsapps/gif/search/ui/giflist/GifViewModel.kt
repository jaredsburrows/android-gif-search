@file:OptIn(
  FlowPreview::class,
  ExperimentalCoroutinesApi::class,
)

package com.burrowsapps.gif.search.ui.giflist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.burrowsapps.gif.search.data.repository.GifRepository
import com.burrowsapps.gif.search.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
internal class GifViewModel
  @Inject
  internal constructor(
    private val repository: GifRepository,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
  ) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    val gifPagingData: Flow<PagingData<GifImageInfo>> =
      searchText
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
          Pager(
            PagingConfig(
              pageSize = 45,
              initialLoadSize = 45,
              prefetchDistance = 15,
            ),
          ) {
            GifPagingSource(repository, dispatcher, query = query.takeIf { it.isNotBlank() })
          }.flow
        }.cachedIn(viewModelScope)

    fun onSearchTextChanged(changedSearchText: String) {
      _searchText.value = changedSearchText
    }

    fun onClearClick() {
      _searchText.value = ""
    }
  }

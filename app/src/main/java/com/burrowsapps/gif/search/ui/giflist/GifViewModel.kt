@file:OptIn(
  FlowPreview::class,
  ExperimentalCoroutinesApi::class,
  ExperimentalPagingApi::class,
)

package com.burrowsapps.gif.search.ui.giflist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.repository.GifRemoteMediator
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
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class GifViewModel
  @Inject
  internal constructor(
    private val repository: GifRepository,
    private val database: AppDatabase,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
  ) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    val gifPagingData: Flow<PagingData<GifImageInfo>> =
      searchText
        .debounce(300) // Wait 300ms after user stops typing
        .map { query ->
          // Normalize the query: trim, lowercase, empty string for trending
          query
            .trim()
            .lowercase()
            .takeIf { it.isNotBlank() }
            .orEmpty()
        }.distinctUntilChanged() // Only emit if the normalized query actually changed
        .flatMapLatest { queryKey ->
          // Create a new Pager for each unique query
          Pager(
            config =
              PagingConfig(
                // 3 columns × 15 rows = 45 items per page
                pageSize = 45,
                // 3 columns × 10 rows - fills screen + buffer for faster startup
                initialLoadSize = 30,
                // Start loading next page 15 items before reaching end
                prefetchDistance = 15,
                // Don't show null placeholders
                enablePlaceholders = false,
                // Don't store more than 200 items in memory
                maxSize = 200,
              ),
            remoteMediator =
              GifRemoteMediator(
                queryKey = queryKey,
                repository = repository,
                database = database,
                dispatcher = dispatcher,
              ),
            pagingSourceFactory = {
              // PagingSource reads from Room Database for smooth scrolling,
              // while RemoteMediator handles network updates in the background
              database.queryResultDao().pagingSource(queryKey)
            },
          ).flow
        }.cachedIn(viewModelScope) // Cache across configuration changes

    fun onSearchTextChanged(changedSearchText: String) {
      _searchText.value = changedSearchText
    }

    fun onClearClick() {
      _searchText.value = ""
    }
  }

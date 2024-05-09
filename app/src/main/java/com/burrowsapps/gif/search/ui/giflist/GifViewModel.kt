package com.burrowsapps.gif.search.ui.giflist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.repository.GifRepository
import com.burrowsapps.gif.search.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GifViewModel
@Inject
internal constructor(
  private val repository: GifRepository,
  @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing: MutableStateFlow<Boolean> = _isRefreshing

  private val _searchText = MutableStateFlow("")
  val searchText: MutableStateFlow<String> = _searchText

  // TODO use states
  private val _uiState = MutableStateFlow<NetworkResult<List<GifImageInfo>>>(NetworkResult.Empty())
  val uiState: StateFlow<NetworkResult<List<GifImageInfo>>> = _uiState

  private val _nextPageResponse = MutableStateFlow("")
  val nextPageResponse: StateFlow<String> = _nextPageResponse

  private val _gifListResponse = MutableStateFlow(emptyList<GifImageInfo>())
  val gifListResponse: StateFlow<List<GifImageInfo>> = _gifListResponse

  init {
    loadTrendingImages()
  }

  override fun onCleared() {
    super.onCleared()
    // Clear the search text
    _searchText.value = ""

    // Clear search results (or trending data if applicable)
    _gifListResponse.value = emptyList()

    // Clear pagination information
    _nextPageResponse.value = ""

    // Reset any loading or UI state, if needed
    _uiState.value = NetworkResult.Empty()
  }

  fun onSearchTextChanged(changedSearchText: String) {
    _searchText.value = changedSearchText
  }

  fun onClearClick() {
    _searchText.value = ""
  }

  fun loadMore() {
    if (searchText.value.isEmpty()) {
      loadTrendingImages(nextPosition = nextPageResponse.value)
    } else {
      loadSearchImages(searchString = searchText.value, nextPosition = nextPageResponse.value)
    }
  }

  fun loadSearchImages(
    searchString: String,
    nextPosition: String? = null,
  ) {
    // TODO update loading states
//    _gifListResponse.value = NetworkResult.Loading()
    viewModelScope.launch(dispatcher) {
      when (val result = repository.getSearchResults(searchString, nextPosition)) {
        is NetworkResult.Empty -> _uiState.value = NetworkResult.Empty()
        is NetworkResult.Error -> _uiState.value = NetworkResult.Error()
        is NetworkResult.Loading -> _uiState.value = NetworkResult.Loading()
        is NetworkResult.Success -> {
          _nextPageResponse.value = result.data?.next.orEmpty()

          if (nextPosition == null) {
            _gifListResponse.value = buildGifList(result.data)
          } else {
            _gifListResponse.value += buildGifList(result.data)
          }
        }
      }
    }
  }

  fun loadTrendingImages(nextPosition: String? = null) {
    // TODO update loading states
//    _trendingResponse.value = NetworkResult.Loading()
    viewModelScope.launch(dispatcher) {
      when (val result = repository.getTrendingResults(nextPosition)) {
        is NetworkResult.Empty -> _uiState.value = NetworkResult.Empty()
        is NetworkResult.Error -> _uiState.value = NetworkResult.Error()
        is NetworkResult.Loading -> _uiState.value = NetworkResult.Loading()
        is NetworkResult.Success -> {
          _nextPageResponse.value = result.data?.next.orEmpty()

          if (nextPosition == null) {
            _gifListResponse.value = buildGifList(result.data)
          } else {
            _gifListResponse.value += buildGifList(result.data)
          }
        }
      }
    }
  }

  private fun buildGifList(response: GifResponseDto?): List<GifImageInfo> {
    return response?.results?.map { result ->
      val media = result.media.first()
      val gif = media.gif
      val tinyGif = media.tinyGif

      GifImageInfo(
        gifUrl = gif.url,
        gifPreviewUrl = gif.preview,
        tinyGifUrl = tinyGif.url,
        tinyGifPreviewUrl = tinyGif.preview,
      )
    }.orEmpty()
  }
}

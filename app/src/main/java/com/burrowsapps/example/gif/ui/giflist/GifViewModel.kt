package com.burrowsapps.example.gif.ui.giflist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burrowsapps.example.gif.data.source.network.GifRepository
import com.burrowsapps.example.gif.data.source.network.NetworkResult
import com.burrowsapps.example.gif.data.source.network.TenorResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GifViewModel @Inject constructor(private val repository: GifRepository) : ViewModel() {
  private val _nextPageResponse = MutableLiveData<String?>()
  val nextPageResponse: LiveData<String?> = _nextPageResponse
  private val _trendingResponse = MutableLiveData<List<GifImageInfo>?>()
  val trendingResponse: LiveData<List<GifImageInfo>?> = _trendingResponse
  private val _searchResponse = MutableLiveData<List<GifImageInfo>?>()
  val searchResponse: LiveData<List<GifImageInfo>?> = _searchResponse

  fun loadTrendingImages(next: String? = null) = viewModelScope.launch {
    repository.getTrendingResults(next).collect { values ->
      when (values) {
        is NetworkResult.Success -> {
          _nextPageResponse.value = values.data?.next

          _trendingResponse.value = buildGifList(values.data)
        }
        is NetworkResult.Error -> {
          _nextPageResponse.value = null

          _trendingResponse.value = null
        }
      }
    }
  }

  fun loadSearchImages(searchString: String, next: String? = null) = viewModelScope.launch {
    repository.getSearchResults(searchString, next).collect { values ->
      when (values) {
        is NetworkResult.Success -> {
          _nextPageResponse.value = values.data?.next

          _searchResponse.value = buildGifList(values.data)
        }
        is NetworkResult.Error -> {
          _nextPageResponse.value = null

          _searchResponse.value = null
        }
      }
    }
  }

  private fun buildGifList(response: TenorResponseDto?): List<GifImageInfo> {
    return response?.results?.map { result ->
      val media = result.media.first()
      val tinyGif = media.tinyGif
      val gif = media.gif
      val gifUrl = gif.url

      Timber.i("buildGifList:\t$gifUrl")

      GifImageInfo(tinyGif.url, tinyGif.preview, gifUrl, gif.preview)
    }.orEmpty()
  }
}

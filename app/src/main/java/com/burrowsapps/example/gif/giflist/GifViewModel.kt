package com.burrowsapps.example.gif.giflist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burrowsapps.example.gif.data.GifRepository
import com.burrowsapps.example.gif.data.remote.NetworkResult
import com.burrowsapps.example.gif.data.remote.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.model.TenorResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifViewModel @Inject constructor(
  private val repository: GifRepository,
) : ViewModel() {
  private val _trendingResponse: MutableLiveData<NetworkResult<TenorResponseDto>> =
    MutableLiveData()
  val trendingResponse: LiveData<NetworkResult<TenorResponseDto>> = _trendingResponse
  private val _searchResponse: MutableLiveData<NetworkResult<TenorResponseDto>> = MutableLiveData()
  val searchResponse: LiveData<NetworkResult<TenorResponseDto>> = _searchResponse

  fun loadTrendingImages(next: Double?) = viewModelScope.launch {
    repository.getTrendingResults(DEFAULT_LIMIT_COUNT, next).collect { values ->
      _trendingResponse.value = values
    }
  }

  fun loadSearchImages(searchString: String, next: Double?) = viewModelScope.launch {
    repository.getSearchResults(searchString, DEFAULT_LIMIT_COUNT, next).collect { values ->
      _searchResponse.value = values
    }
  }
}

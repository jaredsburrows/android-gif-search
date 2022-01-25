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
import javax.inject.Inject

@HiltViewModel
class GifViewModel @Inject constructor(
  private val repository: GifRepository,
) : ViewModel() {
  private val _trendingResponse = MutableLiveData<NetworkResult<TenorResponseDto>>()
  val trendingResponse: LiveData<NetworkResult<TenorResponseDto>> = _trendingResponse
  private val _searchResponse = MutableLiveData<NetworkResult<TenorResponseDto>>()
  val searchResponse: LiveData<NetworkResult<TenorResponseDto>> = _searchResponse

  fun loadTrendingImages(next: String?) = viewModelScope.launch {
    repository.getTrendingResults(next).collect { values ->
      _trendingResponse.value = values
    }
  }

  fun loadSearchImages(searchString: String, next: String?) = viewModelScope.launch {
    repository.getSearchResults(searchString, next).collect { values ->
      _searchResponse.value = values
    }
  }
}

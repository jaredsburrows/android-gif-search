package com.burrowsapps.example.gif.giflist

import android.util.Log
import com.burrowsapps.example.gif.data.TenorService
import com.burrowsapps.example.gif.data.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class GifPresenter @Inject constructor(
  private val service: TenorService,
  @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : GifContract.Presenter {
  private var view: GifContract.View? = null

  override fun takeView(view: GifContract.View) {
    this.view = view
  }

  override fun dropView() {
    view = null
  }

  override fun clearImages() {
    // Clear current data
    view?.clearImages()
  }

  /**
   * Load gif trending images.
   */
  override fun loadTrendingImages(next: Double?) {
    if (view?.isActive() == false) return

    CoroutineScope(dispatcher).launch {
      try {
        val response = service
          .getTrendingResults(DEFAULT_LIMIT_COUNT, next)

        val body = response.body()
        if (response.isSuccessful && body != null) {
          view?.addImages(body)
        } else {
          Log.e(TAG, "onError:\t${response.message()}")
        }
      } catch (e: Exception) {
        Log.e(TAG, "onError", e)
      }
    }
  }

  /**
   * Search gifs based on user input.
   *
   * @param searchString User input.
   */
  override fun loadSearchImages(searchString: String, next: Double?) {
    if (view?.isActive() == false) return

    CoroutineScope(dispatcher).launch {
      try {
        val response = service
          .getSearchResults(searchString, DEFAULT_LIMIT_COUNT, next)

        val body = response.body()
        if (response.isSuccessful && body != null) {
          view?.addImages(body)
        } else {
          Log.e(TAG, "onError:\t${response.message()}")
        }
      } catch (e: Exception) {
        Log.e(TAG, "onError", e)
      }
    }
  }

  companion object {
    private const val TAG = "MainPresenter"
  }
}

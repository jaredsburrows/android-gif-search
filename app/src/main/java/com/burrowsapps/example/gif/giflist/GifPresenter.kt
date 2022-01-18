package com.burrowsapps.example.gif.giflist

import android.util.Log
import com.burrowsapps.example.gif.CoroutineDispatcherProvider
import com.burrowsapps.example.gif.data.RiffsyApiService
import com.burrowsapps.example.gif.data.RiffsyApiService.Companion.DEFAULT_LIMIT_COUNT
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class GifPresenter @Inject constructor(
  private val service: RiffsyApiService,
  private val dispatcher: CoroutineDispatcherProvider,
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

    GlobalScope.launch(dispatcher.io()) {
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

    GlobalScope.launch(dispatcher.io()) {
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

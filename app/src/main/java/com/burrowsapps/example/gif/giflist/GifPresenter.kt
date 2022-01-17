package com.burrowsapps.example.gif.giflist

import android.util.Log
import com.burrowsapps.example.gif.AppCoroutineDispatchers
import com.burrowsapps.example.gif.data.RiffsyApiClient
import com.burrowsapps.example.gif.data.model.RiffsyResponseDto
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GifPresenter @Inject constructor(
  private val client: RiffsyApiClient,
  private val dispatchers: AppCoroutineDispatchers
) : GifContract.Presenter {
  private val disposable = CompositeDisposable()
  private var view: GifContract.View? = null

  override fun takeView(view: GifContract.View) {
    this.view = view
    // TODO load
  }

  override fun dropView() {
    disposable.clear()
    view = null
  }

  override fun clearImages() {
    // Clear current data
    view?.clearImages()
  }

  /**
   * Load gif trending images.
   */
  override suspend fun loadTrendingImages(next: Double?) {
    loadImages(client.getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, next))
  }

  /**
   * Search gifs based on user input.
   *
   * @param searchString User input.
   */
  override suspend fun loadSearchImages(searchString: String, next: Double?) {
    loadImages(
      client
        .getSearchResults(searchString, RiffsyApiClient.DEFAULT_LIMIT_COUNT, next)
    )
  }

  /**
   * Common code for subscription.
   *
   * @param observable Observable to added to the subscription.
   */
  private suspend fun loadImages(observable: RiffsyResponseDto) {
    withContext(dispatchers.io) {

    }

    disposable.add(
      observable
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.ui())
        .subscribe(
          { response ->
            if (view?.isActive() == false) return@subscribe

            // Iterate over data from response and grab the urls
            view?.addImages(response)
          },
          { throwable ->
            Log.e(TAG, "onError", throwable) // java.lang.UnsatisfiedLinkError - unit tests
          }
        )
    )
  }

  companion object {
    private const val TAG = "MainPresenter"
  }
}

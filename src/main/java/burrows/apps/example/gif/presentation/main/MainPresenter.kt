package burrows.apps.example.gif.presentation.main

import android.util.Log
import burrows.apps.example.gif.data.model.RiffsyResponseDto
import burrows.apps.example.gif.data.repository.RiffsyApiClient
import burrows.apps.example.gif.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(val view: MainContract.View,
                    val riffsyApiClient: RiffsyApiClient,
                    val provider: BaseSchedulerProvider) : MainContract.Presenter {
  companion object {
    private const val TAG = "MainPresenter"
  }
  private val disposable = CompositeDisposable()

  init {
    view.setPresenter(this)
  }

  override fun subscribe() {}

  override fun unsubscribe() {
    disposable.clear()
  }

  override fun clearImages() {
    // Clear current data
    view.clearImages()
  }

  /**
   * Load gif trending images.
   */
  override fun loadTrendingImages(next: Double?) {
    loadImages(riffsyApiClient.getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, next))
  }

  /**
   * Search gifs based on user input.
   *
   * @param searchString User input.
   */
  override fun loadSearchImages(searchString: String, next: Double?) {
    loadImages(riffsyApiClient.getSearchResults(searchString, RiffsyApiClient.DEFAULT_LIMIT_COUNT, next))
  }

  /**
   * Common code for subscription.
   *
   * @param observable Observable to added to the subscription.
   */
  fun loadImages(observable: Observable<RiffsyResponseDto>) {
    disposable.add(observable
      .subscribeOn(provider.io())
      .observeOn(provider.ui())
      .subscribe({ riffsyResponse ->
        if (!view.isActive()) return@subscribe

        // Iterate over data from response and grab the urls
        view.addImages(riffsyResponse)
      }, { throwable ->
        Log.e(TAG, "onError", throwable) // java.lang.UnsatisfiedLinkError - unit tests
      }))
  }
}

package burrows.apps.example.gif.giflist

import android.util.Log
import burrows.apps.example.gif.SchedulerProvider
import burrows.apps.example.gif.data.RiffsyApiClient
import burrows.apps.example.gif.data.model.RiffsyResponseDto
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class GifPresenter @Inject constructor(private val riffsyApiClient: RiffsyApiClient,
                                       private val schedulerProvider: SchedulerProvider) : GifContract.Presenter {
  companion object {
    private const val TAG = "MainPresenter"
  }

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
  private fun loadImages(observable: Observable<RiffsyResponseDto>) {
    disposable.add(observable
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.ui())
      .subscribe({ riffsyResponse ->
        if (view?.isActive() == false) return@subscribe

        // Iterate over data from response and grab the urls
        view?.addImages(riffsyResponse)
      }, { throwable ->
        Log.e(TAG, "onError", throwable) // java.lang.UnsatisfiedLinkError - unit tests
      }))
  }
}

package burrows.apps.example.gif.presentation.main

import android.util.Log
import burrows.apps.example.gif.data.rest.model.RiffsyResponse
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.presentation.IBaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MainPresenter(val view: IMainView,
                    val repository: RiffsyApiClient,
                    val provider: IBaseSchedulerProvider) : IMainPresenter {
  private val TAG = MainPresenter::class.java.simpleName // Can't be longer than 23 chars
  private val disposable = CompositeDisposable()

  init {
    this.view.setPresenter(this)
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
  override fun loadTrendingImages(next: Float?) {
    loadImages(repository.getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, next))
  }

  /**
   * Search gifs based on user input.
   *
   * @param searchString User input.
   */
  override fun loadSearchImages(searchString: String, next: Float?) {
    loadImages(repository.getSearchResults(searchString, RiffsyApiClient.DEFAULT_LIMIT_COUNT, next))
  }

  /**
   * Common code for subscription.
   *
   * @param observable Observable to added to the subscription.
   */
  fun loadImages(observable: Observable<RiffsyResponse>) {
    disposable.add(observable
      .subscribeOn(provider.io())
      .observeOn(provider.ui())
      .subscribe(Consumer<RiffsyResponse> { riffsyResponse ->
        if (!view.isActive()) return@Consumer

        // Iterate over data from response and grab the urls
        view.addImages(riffsyResponse)
      }, Consumer<Throwable> { throwable ->
        Log.e(TAG, "onError", throwable) // java.lang.UnsatisfiedLinkError - unit tests
      }) as Disposable)
  }
}

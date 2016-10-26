package burrows.apps.example.gif.presentation.main;

import android.util.Log;
import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.presentation.BaseSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class MainPresenter implements MainContract.Presenter {
  private final CompositeDisposable disposable = new CompositeDisposable();
  final MainContract.View view;
  private final RiffsyRepository repository;
  private final BaseSchedulerProvider provider;

  public MainPresenter(MainContract.View view, RiffsyRepository repository, BaseSchedulerProvider provider) {
    this.view = view;
    this.repository = repository;
    this.provider = provider;

    this.view.setPresenter(this);
  }

  @Override public void subscribe() {
  }

  @Override public void unsubscribe() {
    disposable.clear();
  }

  /**
   * Load gif trending images.
   */
  @Override public void loadTrendingImages() {
    loadImages(repository.getTrendingResults(RiffsyRepository.DEFAULT_LIMIT_COUNT));
  }

  /**
   * Search gifs based on user input.
   *
   * @param searchString User input.
   */
  @Override public void loadSearchImages(String searchString) {
    loadImages(repository.getSearchResults(searchString, RiffsyRepository.DEFAULT_LIMIT_COUNT));
  }

  /**
   * Common code for subscription.
   *
   * @param observable Observable to added to the subscription.
   */
  public void loadImages(Observable<RiffsyResponse> observable) {
    disposable.add(observable
      .subscribeOn(provider.io())
      .observeOn(provider.ui())
      .subscribe(response -> {
        if (!view.isActive()) {
          return;
        }

        // Clear current data
        view.clearImages();

        // Iterate over data from response and grab the urls
        view.addImages(response);
      }, error -> {
        // onError
          Log.e(TAG, "onError", error);
      }, () -> {
        // onComplete
          Log.i(TAG, "Done loading!");
      }));
  }
}

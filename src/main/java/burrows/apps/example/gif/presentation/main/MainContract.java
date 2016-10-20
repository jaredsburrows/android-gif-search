package burrows.apps.example.gif.presentation.main;

import burrows.apps.example.gif.presentation.BasePresenter;
import burrows.apps.example.gif.presentation.BaseView;
import burrows.apps.example.gif.data.rest.model.RiffsyResponse;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class MainContract {
  // View
  interface View extends BaseView<Presenter> {
    void clearImages();
    void addImages(RiffsyResponse response);
    void showDialog(String url);
    boolean isActive();
  }

  // Presenter
  interface Presenter extends BasePresenter {
    void loadTrendingImages();
    void loadSearchImages(String searchString);
  }
}

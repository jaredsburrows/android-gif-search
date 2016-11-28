package burrows.apps.example.gif.presentation.main;

import burrows.apps.example.gif.presentation.IBasePresenter;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
interface IMainPresenter extends IBasePresenter {
  void clearImages();
  void loadTrendingImages(Float next);
  void loadSearchImages(String searchString, Float next);
}

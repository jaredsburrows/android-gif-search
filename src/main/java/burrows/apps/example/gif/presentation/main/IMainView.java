package burrows.apps.example.gif.presentation.main;

import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import burrows.apps.example.gif.presentation.IBaseView;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
interface IMainView extends IBaseView<IMainPresenter> {
  void clearImages();
  void addImages(RiffsyResponse response);
  void showDialog(ImageInfoModel url);
  boolean isActive();
}

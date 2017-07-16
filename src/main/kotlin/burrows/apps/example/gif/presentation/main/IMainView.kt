package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.data.rest.model.RiffsyResponseDto
import burrows.apps.example.gif.presentation.IBaseView
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface IMainView : IBaseView<IMainPresenter> {
  fun clearImages()
  fun addImages(response: RiffsyResponseDto)
  fun showDialog(imageInfoModel: ImageInfoModel)
  fun isActive(): Boolean
}

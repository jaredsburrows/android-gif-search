package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.data.rest.model.RiffsyResponseDto
import burrows.apps.example.gif.presentation.BasePresenter
import burrows.apps.example.gif.presentation.BaseView
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface MainContract {
  interface MainPresenter : BasePresenter {
    fun clearImages()
    fun loadTrendingImages(next: Double?)
    fun loadSearchImages(searchString: String, next: Double?)
  }

  interface MainView : BaseView<MainPresenter> {
    fun clearImages()
    fun addImages(riffsyResponseDto: RiffsyResponseDto)
    fun showDialog(imageInfoModel: ImageInfoModel)
    fun isActive(): Boolean
  }
}

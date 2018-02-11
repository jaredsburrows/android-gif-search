package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.data.model.RiffsyResponseDto
import burrows.apps.example.gif.BasePresenter
import burrows.apps.example.gif.BaseView
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel

interface MainContract {
  interface Presenter : BasePresenter {
    fun clearImages()
    fun loadTrendingImages(next: Double?)
    fun loadSearchImages(searchString: String, next: Double?)
  }

  interface View : BaseView<Presenter> {
    fun clearImages()
    fun addImages(riffsyResponseDto: RiffsyResponseDto)
    fun showDialog(imageInfoModel: ImageInfoModel)
    fun isActive(): Boolean
  }
}

package burrows.apps.example.gif.giflist

import burrows.apps.example.gif.data.model.RiffsyResponseDto
import burrows.apps.example.gif.BasePresenter
import burrows.apps.example.gif.BaseView

interface GifContract {
  interface Presenter : BasePresenter<View> {
    fun clearImages()
    fun loadTrendingImages(next: Double?)
    fun loadSearchImages(searchString: String, next: Double?)
  }

  interface View : BaseView {
    fun clearImages()
    fun addImages(riffsyResponseDto: RiffsyResponseDto)
    fun showDialog(imageInfoModel: GifImageInfo)
  }
}

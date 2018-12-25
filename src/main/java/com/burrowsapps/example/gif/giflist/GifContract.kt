package com.burrowsapps.example.gif.giflist

import com.burrowsapps.example.gif.BasePresenter
import com.burrowsapps.example.gif.BaseView
import com.burrowsapps.example.gif.data.model.RiffsyResponseDto

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

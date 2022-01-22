package com.burrowsapps.example.gif.ui.giflist

import com.burrowsapps.example.gif.BasePresenter
import com.burrowsapps.example.gif.BaseView
import com.burrowsapps.example.gif.data.source.network.TenorResponseDto

interface GifContract {
  interface Presenter : BasePresenter<View> {
    fun clearImages()
    fun loadTrendingImages(next: Double?)
    fun loadSearchImages(searchString: String, next: Double?)
  }

  interface View : BaseView {
    fun clearImages()
    fun addImages(responseDto: TenorResponseDto)
    fun showDialog(imageInfo: GifImageInfo)
  }
}

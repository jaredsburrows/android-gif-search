package com.burrowsapps.example.gif.data

import android.content.Context
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.di.GlideApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageService @Inject constructor(@ApplicationContext private val context: Context) {
  private val imageHeight = context.resources.getDimensionPixelSize(R.dimen.gif_image_width)
  private val imageWidth = imageHeight

  fun load(url: String): RequestBuilder<GifDrawable> {
    return GlideApp.with(context)
      .asGif()
      .apply(
        RequestOptions.noTransformation()
          .override(imageWidth, imageHeight)
      )
      .load(url)
  }
}

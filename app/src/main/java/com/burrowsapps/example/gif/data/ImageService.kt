package com.burrowsapps.example.gif.data

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy.RESOURCE
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.burrowsapps.example.gif.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageService @Inject constructor(@ApplicationContext private val context: Context) {
  private val imageHeight = context.resources.getDimensionPixelSize(R.dimen.gif_image_width)
  private val imageWidth = imageHeight

  fun load(url: String): RequestBuilder<GifDrawable> {
    return Glide.with(context)
      .asGif()
      .apply(
        RequestOptions.noTransformation()
          .error(R.mipmap.ic_launcher)
          .fallback(R.mipmap.ic_launcher)
          .override(imageWidth, imageHeight)
          .diskCacheStrategy(RESOURCE)
      )
      .load(url)
  }
}

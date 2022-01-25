package com.burrowsapps.example.gif.data

import android.content.Context
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.burrowsapps.example.gif.di.GlideApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageService @Inject constructor(@ApplicationContext private val context: Context) {
  fun load(url: String): RequestBuilder<GifDrawable> {
    return GlideApp.with(context)
      .asGif()
      .load(url)
  }
}

package com.burrowsapps.gif.search.data

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.signature.ObjectKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageService @Inject internal constructor(@ApplicationContext private val context: Context) {

  fun loadGif(
    imageUrl: String,
    thumbnailUrl: String,
    override: Int = SIZE_ORIGINAL,
  ): RequestBuilder<GifDrawable> {
    val request = Glide.with(context).asGif()
    return request
      .transition(withCrossFade())
      .thumbnail(
        request
          .transition(withCrossFade())
          .load(thumbnailUrl)
          .override(override)
          .signature(ObjectKey(thumbnailUrl)),
      )
      .override(override)
      .signature(ObjectKey(imageUrl))
  }
}

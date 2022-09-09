package com.burrowsapps.example.gif.data

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.burrowsapps.example.gif.di.GlideApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageService @Inject internal constructor(@ApplicationContext private val context: Context) {

  fun loadGif(
    imageUrl: String,
    thumbnailUrl: String? = null,
    override: Int = SIZE_ORIGINAL,
    onLoadStarted: () -> Unit = {},
    onResourceReady: (GifDrawable?) -> Unit = {},
    onLoadFailed: () -> Unit = {},
  ) {
    loadGifRequest(imageUrl)
      .thumbnail(loadGifRequest(thumbnailUrl))
      .override(override)
      .signature(ObjectKey(imageUrl))
      .into(object : CustomTarget<GifDrawable>() {
        override fun onLoadStarted(placeholder: Drawable?) {
          super.onLoadStarted(placeholder)
          onLoadStarted.invoke()
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
          super.onLoadFailed(errorDrawable)
          onLoadFailed.invoke()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
          onLoadFailed.invoke()
        }

        override fun onResourceReady(
          resource: GifDrawable,
          transition: Transition<in GifDrawable>?,
        ) {
          onResourceReady.invoke(resource)
        }
      })
  }

  private fun loadGifRequest(imageUrl: String?): RequestBuilder<GifDrawable> {
    return GlideApp.with(context)
      .asGif()
      .transition(withCrossFade())
      .load(imageUrl)
  }
}

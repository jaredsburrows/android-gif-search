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
class ImageService @Inject constructor(@ApplicationContext private val context: Context) {

  fun loadGif(
    imageUrl: String,
    thumbnailUrl: String? = null,
    override: Int = SIZE_ORIGINAL,
    onResourceReady: (GifDrawable?) -> Unit,
    onLoadFailed: () -> Unit,
  ) {
    loadGif(imageUrl)
      .override(override)
      .signature(ObjectKey(imageUrl))
      .thumbnail(loadGif(thumbnailUrl))
      .into(object : CustomTarget<GifDrawable>() {
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

  private fun loadGif(imageUrl: String?): RequestBuilder<GifDrawable> {
    return GlideApp.with(context)
      .asGif()
      .transition(withCrossFade())
      .load(imageUrl)
  }
}

package com.burrowsapps.example.gif.data

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.burrowsapps.example.gif.di.GlideApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageService @Inject constructor(@ApplicationContext private val context: Context) {

  fun loadGif(
    imageUrl: String,
    thumbnailUrl: String,
    onResourceReady: (GifDrawable?) -> Unit,
    onLoadFailed: () -> Unit,
  ) {
    loadGif(imageUrl)
      .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
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

  fun loadGif(
    imageUrl: String,
    thumbnailUrl: String,
    imageView: ImageView,
    onResourceReady: () -> Unit,
    onLoadFailed: (GlideException?) -> Unit,
  ) {
    loadGif(imageUrl)
      .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
      .thumbnail(loadGif(thumbnailUrl))
      .listener(
        object : RequestListener<GifDrawable> {
          override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            onResourceReady.invoke()
            return false
          }

          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
          ): Boolean {
            onLoadFailed.invoke(e)
            return false
          }
        }
      )
      .into(imageView)
      .clearOnDetach()
  }

  private fun loadGif(imageUrl: String): RequestBuilder<GifDrawable> {
    return GlideApp.with(context)
      .asGif()
      .transition(withCrossFade())
      .load(imageUrl)
  }
}

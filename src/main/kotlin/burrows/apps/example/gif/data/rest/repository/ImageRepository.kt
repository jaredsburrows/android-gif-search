package burrows.apps.example.gif.data.rest.repository

import android.content.Context
import burrows.apps.example.gif.R
import com.bumptech.glide.GifRequestBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
open class ImageRepository(val context: Context) {
  private val imageHeight: Int = context.resources.getDimensionPixelSize(R.dimen.gif_image_width)
  private val imageWidth: Int

  init {
    this.imageWidth = imageHeight
  }

  open fun <T> load(url: T): GifRequestBuilder<T> {
    return Glide.with(context)
      .load(url)
      .asGif()
      .error(R.mipmap.ic_launcher)
      .fallback(R.mipmap.ic_launcher)
      .override(imageWidth, imageHeight)
      // https://github.com/bumptech/glide/issues/600#issuecomment-135541121
      .diskCacheStrategy(DiskCacheStrategy.SOURCE)
  }
}

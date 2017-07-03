package burrows.apps.example.gif.data.rest.repository

import android.content.Context
import burrows.apps.example.gif.R
import com.bumptech.glide.GifRequestBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import javax.inject.Inject

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ImageApiRepository {
  private val context: Context
  private val imageHeight: Int
  private val imageWidth: Int

  @Suppress("ConvertSecondaryConstructorToPrimary")
  @Inject constructor(context: Context) {
    this.context = context

    this.imageHeight = context.resources.getDimensionPixelSize(R.dimen.gif_image_width)
    this.imageWidth = imageHeight
  }

  fun <T> load(url: T): GifRequestBuilder<T> {
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

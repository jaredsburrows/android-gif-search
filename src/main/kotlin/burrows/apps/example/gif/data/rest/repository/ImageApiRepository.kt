package burrows.apps.example.gif.data.rest.repository

import android.content.Context
import burrows.apps.example.gif.R
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import javax.inject.Inject

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
open class ImageApiRepository @Inject constructor(private val context: Context) { // dexmaker-mockito issue, leave open
  private val imageHeight: Int = context.resources.getDimensionPixelSize(R.dimen.gif_image_width)
  private val imageWidth: Int

  init {
    this.imageWidth = imageHeight
  }

  open fun load(url: String?): RequestBuilder<GifDrawable> {
    return Glide.with(context)
      .asGif()
      .apply(RequestOptions.noTransformation()
          .error(R.mipmap.ic_launcher)
          .fallback(R.mipmap.ic_launcher)
          .override(imageWidth, imageHeight)
          .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
      .load(url)
  }
}

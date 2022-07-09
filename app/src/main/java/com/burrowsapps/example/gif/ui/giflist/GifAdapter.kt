package com.burrowsapps.example.gif.ui.giflist

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.di.GlideApp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.android.material.composethemeadapter.MdcTheme
import timber.log.Timber

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 */
class GifAdapter(
  private var onItemClick: (GifImageInfo) -> Unit,
  private val imageService: ImageService
) : RecyclerView.Adapter<GifViewHolder>() {
  private val data = mutableListOf<GifImageInfo>()

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): GifViewHolder {
    return GifViewHolder(ComposeView(parent.context), onItemClick, imageService)
  }

  override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
    val imageInfoModel = getItem(position)

    holder.bind(imageInfoModel)
  }

  override fun onViewRecycled(holder: GifViewHolder) {
    super.onViewRecycled(holder)

    // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
    // Forget view, try to free resources
//    GlideApp.with(holder.itemView.context).clear(holder.binding.gifImage)
    for (view in holder.composeView.children) {
      GlideApp.with(holder.itemView.context).clear(view)
    }
    GlideApp.with(holder.itemView.context).clear(holder.composeView)
//    holder.binding.apply {
//      gifImage.setImageDrawable(null)
//      // Make sure to show progress when loading new view
//      gifProgress.show()
//    }
    Timber.i("onViewRecycled:\t$holder")

    // when RecyclerView has recycled this ViewHolder
    holder.composeView.disposeComposition()
  }

  override fun onFailedToRecycleView(holder: GifViewHolder): Boolean {
    Timber.e("onFailedToRecycleView:\t$holder")
    return false
  }

  override fun getItemCount() = data.size

  override fun getItemId(position: Int) = getItem(position).hashCode().toLong()

  fun getItem(location: Int) = data[location]

  fun add(model: List<GifImageInfo>) {
    data.addAll(model)
    notifyItemRangeInserted(data.size, data.size + 1)
  }

  fun clear() {
    val size = data.size
    if (size > 0) {
      for (i in 0 until size) data.removeAt(0)

      notifyItemRangeRemoved(0, size)
    }
  }
}

class GifViewHolder(
  val composeView: ComposeView,
  var onItemClick: (GifImageInfo) -> Unit,
  val imageService: ImageService,
) : RecyclerView.ViewHolder(composeView) {
  init {
    composeView.setViewCompositionStrategy(
      ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
    )
  }

  fun bind(imageInfoModel: GifImageInfo) {
    itemView.setOnClickListener { onItemClick.invoke(imageInfoModel) }

    composeView.setContent {
      val showProgressBar = remember { mutableStateOf(true) }

      MdcTheme {
        val state = remember { mutableStateOf<GifDrawable?>(null) }

        // Load images - 'tinyGifPreviewUrl' -> 'tinyGifUrl'
        imageService.loadGif(imageInfoModel.tinyGifUrl)
          .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
          .thumbnail(imageService.loadGif(imageInfoModel.tinyGifPreviewUrl))
          .listener(
            object : RequestListener<GifDrawable> {
              override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: Target<GifDrawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
              ): Boolean {
                // Hide progressbar
                showProgressBar.value = false
                Timber.i("onResourceReady:\t$model")

                return false
              }

              override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>?,
                isFirstResource: Boolean
              ): Boolean {
                // Hide progressbar
                showProgressBar.value = false
                Timber.e(e, "onLoadFailed:\t$model")

                return false
              }
            }
          )
          .into(object : CustomTarget<GifDrawable>() {
            override fun onLoadCleared(p: Drawable?) {
              state.value = null
            }

            override fun onResourceReady(
              resource: GifDrawable,
              transition: Transition<in GifDrawable>?,
            ) {
              state.value = resource
            }
          })


        if (showProgressBar.value) {
          CircularProgressIndicator(
            modifier = Modifier
              .fillMaxWidth()
              .height(128.dp)
              .padding(all = 24.dp)
          )
        } else {
          Image(
            painter = rememberDrawablePainter(drawable = state.value),
            contentDescription = stringResource(id = R.string.gif_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .height(135.dp)
          )
        }

      }
    }
  }
}

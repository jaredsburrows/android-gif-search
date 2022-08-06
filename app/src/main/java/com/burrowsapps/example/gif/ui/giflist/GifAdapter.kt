package com.burrowsapps.example.gif.ui.giflist

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.ui.theme.GifTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 */
class GifAdapter(
  private val imageService: ImageService,
  private var onItemClick: (GifImageInfo) -> Unit,
) : RecyclerView.Adapter<GifAdapter.ViewHolder>() {
  private val data = mutableListOf<GifImageInfo>()

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ) = ViewHolder(ComposeView(parent.context))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val imageInfoModel = getItem(position)

    holder.bind(imageInfoModel)
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

  inner class ViewHolder(
    private val composeView: ComposeView,
  ) : RecyclerView.ViewHolder(composeView) {
    fun bind(imageInfoModel: GifImageInfo) {
      itemView.setOnClickListener { onItemClick.invoke(imageInfoModel) }

      composeView.setContent {
        val showProgressBar = remember { mutableStateOf(true) }
        val state = remember { mutableStateOf<GifDrawable?>(null) }

        GifTheme {
          // Load images - 'tinyGifPreviewUrl' -> 'tinyGifUrl'
          imageService.loadGif(
            imageUrl = imageInfoModel.tinyGifUrl,
            thumbnailUrl = imageInfoModel.tinyGifPreviewUrl,
            onResourceReady = { resource ->
              showProgressBar.value = false
              state.value = resource
            },
            onLoadFailed = {
              showProgressBar.value = false
              state.value = null
            },
          )

          // Show loading indicator when image is not loaded
          if (showProgressBar.value) {
            CircularProgressIndicator(
              modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .padding(all = 24.dp),
            )
          } else {
            Image(
              painter = rememberDrawablePainter(drawable = state.value),
              contentDescription = stringResource(id = R.string.gif_image),
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .fillMaxWidth()
                .height(135.dp),
            )
          }
        }
      }
    }
  }
}

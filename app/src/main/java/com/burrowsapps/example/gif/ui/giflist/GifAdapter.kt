package com.burrowsapps.example.gif.ui.giflist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.databinding.ListItemBinding
import com.burrowsapps.example.gif.di.GlideApp
import timber.log.Timber

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 */
class GifAdapter(
  private var onItemClick: (GifImageInfo) -> Unit,
  private val imageService: ImageService
) : RecyclerView.Adapter<GifAdapter.ViewHolder>() {
  private val data = mutableListOf<GifImageInfo>()

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ) = ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val imageInfoModel = getItem(position)

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
            holder.binding.gifProgress.hide()
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
            holder.binding.gifProgress.hide()
            Timber.e(e, "onLoadFailed:\t$model")

            return false
          }
        }
      )
      .into(holder.binding.gifImage)
      .clearOnDetach()

    holder.itemView.setOnClickListener { onItemClick.invoke(imageInfoModel) }
  }

  override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)

    // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
    // Forget view, try to free resources
    GlideApp.with(holder.itemView.context).clear(holder.binding.gifImage)
    holder.binding.apply {
      gifImage.setImageDrawable(null)
      // Make sure to show progress when loading new view
      gifProgress.show()
    }
    Timber.i("onViewRecycled:\t$holder")
    Timber.i("onViewRecycled:\t${holder.binding.gifImage}")
  }

  override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
    Timber.e("onFailedToRecycleView:\t$holder")
    Timber.e("onFailedToRecycleView:\t${holder.binding.gifImage}")
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

  inner class ViewHolder(
    internal val binding: ListItemBinding
  ) : RecyclerView.ViewHolder(binding.root)
}

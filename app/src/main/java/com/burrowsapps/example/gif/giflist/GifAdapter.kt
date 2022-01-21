package com.burrowsapps.example.gif.giflist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.burrowsapps.example.gif.data.remote.GifImageLoader
import com.burrowsapps.example.gif.databinding.ListItemBinding
import com.burrowsapps.example.gif.di.GlideApp

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 */
class GifAdapter(
  private val onItemClickListener: OnItemClickListener,
  private val imageService: GifImageLoader
) : RecyclerView.Adapter<GifAdapter.ViewHolder>() {
  private val data = arrayListOf<GifImageInfo>()

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ) = ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val imageInfoModel = getItem(position)

    // Load images
    imageService.load(imageInfoModel.url)
      .thumbnail(imageService.load(imageInfoModel.previewUrl))
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
            holder.binding.gifProgress.visibility = View.GONE
            if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

            return false
          }

          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
          ): Boolean {
            // Hide progressbar
            holder.binding.gifProgress.visibility = View.GONE
            if (Log.isLoggable(TAG, Log.ERROR)) Log.e(TAG, "finished loading\t $model", e)

            return false
          }
        }
      )
      .into(holder.binding.gifImage)

    holder.itemView.setOnClickListener { onItemClickListener.onClick(imageInfoModel) }
  }

  override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)

    // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
    // Forget view, try to free resources
    GlideApp.with(holder.itemView.context).clear(holder.binding.gifImage)
    holder.binding.apply {
      gifImage.setImageDrawable(null)
      // Make sure to show progress when loading new view
      gifProgress.visibility = View.VISIBLE
    }
  }

  override fun getItemCount() = data.size

  override fun getItemId(position: Int) = getItem(position).url.hashCode().toLong()

  fun getItem(location: Int) = data[location]

  fun clear() {
    val size = data.size
    if (size > 0) {
      for (i in 0 until size) data.removeAt(0)

      notifyItemRangeRemoved(0, size)
    }
  }

  fun add(model: GifImageInfo) = data.add(model).apply {
    notifyItemInserted(data.size + 1)
  }

  fun addAll(collection: List<GifImageInfo>) = data.addAll(collection).apply {
    notifyItemRangeInserted(0, data.size + 1)
  }

  fun add(location: Int, model: GifImageInfo) {
    data.add(location, model)
    notifyItemInserted(location)
  }

  inner class ViewHolder(
    internal val binding: ListItemBinding
  ) : RecyclerView.ViewHolder(binding.root)

  interface OnItemClickListener {
    fun onClick(imageInfoModel: GifImageInfo)
  }

  companion object {
    private const val TAG = "GifAdapter"
  }
}

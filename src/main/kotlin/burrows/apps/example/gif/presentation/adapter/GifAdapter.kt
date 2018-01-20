package burrows.apps.example.gif.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import burrows.apps.example.gif.R
import burrows.apps.example.gif.data.repository.ImageApiRepository
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class GifAdapter(
  private val onItemClickListener: GifAdapter.OnItemClickListener,
  private val repository: ImageApiRepository) : RecyclerView.Adapter<GifAdapter.ViewHolder>() {
  companion object {
    private val TAG = "GifAdapter"
  }
  private val data = arrayListOf<ImageInfoModel>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val imageInfoModel = getItem(position)

    // Load images
    repository.load(imageInfoModel.url)
      .thumbnail(repository.load(imageInfoModel.previewUrl))
      .listener(object : RequestListener<GifDrawable> {
        override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
                                     dataSource: DataSource?, isFirstResource: Boolean): Boolean {
          // Hide progressbar
          holder.itemView.gifProgress.visibility = View.GONE
          if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

          return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?,
                                  isFirstResource: Boolean): Boolean {
          // Hide progressbar
          holder.itemView.gifProgress.visibility = View.GONE
          if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

          return false
        }
      })
      .into(holder.itemView.gifImage)

    holder.itemView.setOnClickListener { onItemClickListener.onClick(imageInfoModel) }
  }

  override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)

    // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
    // Forget view, try to free resources
    Glide.with(holder.itemView.context).clear(holder.itemView.gifImage)
    holder.itemView.gifImage.setImageDrawable(null)
    // Make sure to show progress when loading new view
    holder.itemView.gifProgress.visibility = View.VISIBLE
  }

  /**
   * Returns the number of elements in the data.
   *
   * @return the number of elements in the data.
   */
  override fun getItemCount(): Int = data.size

  /**
   * Returns the hashCode of the URL of image.
   *
   * @return the hashCode of the URL of image.
   */
  override fun getItemId(position: Int): Long = getItem(position).url?.hashCode()?.toLong() ?: 0L

  /**
   * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
   */
  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

  /**
   * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
   */
  interface OnItemClickListener {
    fun onClick(imageInfoModel: ImageInfoModel)
  }

  /**
   * Returns the element at the specified location in the data.
   *
   * @param location the index of the element to return.
   * @return the element at the specified location.
   */
  fun getItem(location: Int): ImageInfoModel = data[location]

  /**
   * Searches the data for the specified object and returns the index of the
   * first occurrence.
   *
   * @param model the object to search for.
   * @return the index of the first occurrence of the object or -1 if the object was not found.
   */
  fun getLocation(model: ImageInfoModel): Int = data.indexOf(model)

  /**
   * Clear the entire adapter using [android.support.v7.widget.RecyclerView.Adapter.notifyItemRangeRemoved].
   */
  fun clear() {
    val size = data.size
    if (size > 0) {
      for (i in 0 until size) data.removeAt(0)

      notifyItemRangeRemoved(0, size)
    }
  }

  /**
   * Adds the specified object at the end of the data.
   *
   * @param model the object to add.
   * @return always true.
   */
  fun add(model: ImageInfoModel): Boolean {
    return data.add(model).apply {
      notifyItemInserted(data.size + 1)
    }
  }

  /**
   * Adds the objects in the specified collection to the end of the data. The
   * objects are added in the order in which they are returned from the
   * collection's iterator.
   *
   * @param collection the collection of objects.
   * @return `true` if the data is modified, `false` otherwise (i.e. if the passed
   * collection was empty).
   */
  fun addAll(collection: List<ImageInfoModel>): Boolean {
    return data.addAll(collection).apply {
      notifyItemRangeInserted(0, data.size + 1)
    }
  }

  /**
   * Inserts the specified object into the data at the specified location.
   * The object is inserted before the current element at the specified
   * location. If the location is equal to the size of the data, the object
   * is added at the end. If the location is smaller than the size of the
   * data, then all elements beyond the specified location are moved by one
   * location towards the end of the data.
   *
   * @param location the index at which to insert.
   * @param model the object to add.
   */
  fun add(location: Int, model: ImageInfoModel) {
    data.add(location, model)
    notifyItemInserted(location)
  }

  /**
   * Removes the first occurrence of the specified object from the data.
   *
   * @param model the object to remove.
   * @return true if the data was modified by this operation, false otherwise.
   */
  fun remove(location: Int, model: ImageInfoModel): Boolean {
    return data.remove(model).apply {
      notifyItemRangeRemoved(location, data.size)
    }
  }

  /**
   * Removes the first occurrence of the specified object from the data.
   *
   * @param model the object to remove.
   * @return true if the data was modified by this operation, false otherwise.
   */
  fun remove(model: ImageInfoModel): Boolean {
    return getLocation(model).let { location ->
      data.remove(model).apply {
        notifyItemRemoved(location)
      }
    }
  }

  /**
   * Removes the object at the specified location from the data.
   *
   * @param location the index of the object to remove.
   * @return the removed object.
   */
  fun remove(location: Int): ImageInfoModel {
    return data.removeAt(location).apply {
      notifyItemRemoved(location)
      notifyItemRangeChanged(location, data.size)
    }
  }
}

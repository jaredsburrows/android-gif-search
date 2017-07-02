package burrows.apps.example.gif.presentation.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.data.rest.repository.ImageApiRepository;
import burrows.apps.example.gif.databinding.ListItemBinding;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GifAdapter extends RecyclerView.Adapter<GifAdapter.ViewHolder> {
  // Can't be longer than 23 chars
  private final static String TAG = GifAdapter.class.getSimpleName();
  private final List<ImageInfoModel> data = new ArrayList<>();
  private final ImageApiRepository repository;
  private final OnItemClickListener onItemClickListener;

  public GifAdapter(OnItemClickListener onItemClickListener, ImageApiRepository repository) {
    this.onItemClickListener = onItemClickListener;
    this.repository = repository;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(
      DataBindingUtil.<ListItemBinding>inflate(LayoutInflater.from(parent.getContext()),
        R.layout.list_item, parent, false));
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    final ImageInfoModel imageInfo = getItem(position);
    final ListItemBinding binding = holder.binding;

    // Load images
    repository.load(imageInfo.getUrl())
      .thumbnail(repository.load(imageInfo.getPreviewUrl()))
      .listener(new RequestListener<Object, GifDrawable>() {
        @Override public boolean onException(Exception e, Object model, Target<GifDrawable> target,
          boolean isFirstResource) {
          // Hide progressbar
          binding.gifProgress.setVisibility(View.GONE);
          if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t" + model);

          return false;
        }

        @Override public boolean onResourceReady(GifDrawable resource, Object model,
          Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
          // Hide progressbar
          binding.gifProgress.setVisibility(View.GONE);
          if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t" + model);

          return false;
        }
      })
      .into(binding.gifImage);

    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        onItemClickListener.onClick(imageInfo);
      }
    });

    binding.executePendingBindings();
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);

    // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
    // Forget view, try to free resources
    Glide.clear(holder.binding.gifImage);
    holder.binding.gifImage.setImageDrawable(null);
    // Make sure to show progress when loading new view
    holder.binding.gifProgress.setVisibility(View.VISIBLE);
  }

  /**
   * Returns the number of elements in the data.
   *
   * @return the number of elements in the data.
   */
  @Override public int getItemCount() {
    return data.size();
  }

  /**
   * Returns the hashCode of the URL of image.
   *
   * @return the hashCode of the URL of image.
   */
  @Override public long getItemId(int position) {
    return getItem(position).getUrl().hashCode();
  }

  /**
   * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
   */
  final static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    final ListItemBinding binding;

    ViewHolder(ListItemBinding binding) {
      super(binding.getRoot());

      this.binding = binding;
    }
  }

  /**
   * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
   */
  public interface OnItemClickListener {
    void onClick(ImageInfoModel imageInfo);
  }

  /**
   * Returns the element at the specified location in the data.
   *
   * @param location the index of the element to return.
   * @return the element at the specified location.
   * @throws IndexOutOfBoundsException if {@code location < 0 || location >= size()}
   */
  public ImageInfoModel getItem(int location) {
    return data.get(location);
  }

  /**
   * Searches the data for the specified object and returns the index of the
   * first occurrence.
   *
   * @param object the object to search for.
   * @return the index of the first occurrence of the object or -1 if the object was not found.
   */
  public int getLocation(ImageInfoModel object) {
    return data.indexOf(object);
  }

  /**
   * Clear the entire adapter using {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRangeRemoved}.
   */
  public void clear() {
    final int size = data.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        data.remove(0);
      }

      notifyItemRangeRemoved(0, size);
    }
  }

  /**
   * Adds the specified object at the end of the data.
   *
   * @param object the object to add.
   * @return always true.
   * @throws UnsupportedOperationException if adding to the data is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this data.
   * @throws IllegalArgumentException if the object cannot be added to the data.
   */
  public boolean add(ImageInfoModel object) {
    final boolean added = data.add(object);
    notifyItemInserted(data.size() + 1);
    return added;
  }

  /**
   * Adds the objects in the specified collection to the end of the data. The
   * objects are added in the order in which they are returned from the
   * collection's iterator.
   *
   * @param collection the collection of objects.
   * @return {@code true} if the data is modified, {@code false} otherwise (i.e. if the passed
   * collection was empty).
   * @throws UnsupportedOperationException if adding to the data is not supported.
   * @throws ClassCastException if the class of an object is inappropriate for this data.
   * @throws IllegalArgumentException if an object cannot be added to the data.
   */
  public boolean addAll(List<ImageInfoModel> collection) {
    final boolean added = data.addAll(collection);
    notifyItemRangeInserted(0, data.size() + 1);
    return added;
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
   * @param object the object to add.
   * @throws UnsupportedOperationException if adding to the data is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this data.
   * @throws IllegalArgumentException if the object cannot be added to the data.
   * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
   */
  public void add(int location, ImageInfoModel object) {
    data.add(location, object);
    notifyItemInserted(location);
  }

  /**
   * Removes the first occurrence of the specified object from the data.
   *
   * @param object the object to remove.
   * @return true if the data was modified by this operation, false otherwise.
   * @throws UnsupportedOperationException if removing from the data is not supported.
   */
  public boolean remove(int location, ImageInfoModel object) {
    final boolean removed = data.remove(object);
    notifyItemRangeRemoved(location, data.size());
    return removed;
  }

  /**
   * Removes the first occurrence of the specified object from the data.
   *
   * @param object the object to remove.
   * @return true if the data was modified by this operation, false otherwise.
   * @throws UnsupportedOperationException if removing from the data is not supported.
   */
  public boolean remove(ImageInfoModel object) {
    final int location = getLocation(object);
    final boolean removed = data.remove(object);
    notifyItemRemoved(location);
    return removed;
  }

  /**
   * Removes the object at the specified location from the data.
   *
   * @param location the index of the object to remove.
   * @return the removed object.
   * @throws UnsupportedOperationException if removing from the data is not supported.
   * @throws IndexOutOfBoundsException if {@code location < 0 || location >= size()}
   */
  public ImageInfoModel remove(int location) {
    final ImageInfoModel removedObject = data.remove(location);
    notifyItemRemoved(location);
    notifyItemRangeChanged(location, data.size());
    return removedObject;
  }
}

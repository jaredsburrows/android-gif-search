package burrows.apps.gif.example.ui.adapter;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import burrows.apps.gif.example.App;
import burrows.apps.gif.example.R;
import burrows.apps.gif.example.rest.service.ImageDownloader;
import burrows.apps.gif.example.rx.RxBus;
import burrows.apps.gif.example.rx.event.PreviewImageEvent;
import burrows.apps.gif.example.ui.adapter.model.ImageInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import pl.droidsonroids.gif.GifImageView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GifAdapter extends RecyclerView.Adapter<GifAdapter.GifAdapterViewHolder> {
  private final List<ImageInfo> data = new ArrayList<>();
  @Inject RxBus rxBus;
  @Inject ImageDownloader imageDownloader;

  public GifAdapter(Application application) {
    // Injection dependencies
    ((App) application).getNetComponent().inject(this);
  }

  @Override public GifAdapterViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new GifAdapterViewHolder(LayoutInflater.from(parent.getContext())
      .inflate(R.layout.recyclerview_list_item, parent, false));
  }

  @Override public void onBindViewHolder(GifAdapterViewHolder holder, int position) {
    final String url = getItem(position).getUrl();

    imageDownloader.load(url, holder.gifImageView, holder.progressBar);

    holder.itemView.setOnClickListener(view -> rxBus.send(new PreviewImageEvent(url)));
  }

  @Override public void onViewRecycled(GifAdapterViewHolder holder) {
    super.onViewRecycled(holder);

    Glide.clear(holder.gifImageView);
    holder.gifImageView.setImageDrawable(null);
  }

  /**
   * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
   */
  final class GifAdapterViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.gif_progress) ProgressBar progressBar;
    @BindView(R.id.gif_image) GifImageView gifImageView;

    GifAdapterViewHolder(View view) {
      super(view);

      ButterKnife.bind(this, view);
    }
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
   * Returns the instance of the data.
   *
   * @return instance of the data.
   */
  public List<ImageInfo> getList() {
    return data;
  }

  /**
   * Returns the element at the specified location in the data.
   *
   * @param location the index of the element to return.
   * @return the element at the specified location.
   * @throws IndexOutOfBoundsException if {@code location < 0 || location >= size()}
   */
  public ImageInfo getItem(int location) {
    return data.get(location);
  }

  /**
   * Searches the data for the specified object and returns the index of the
   * first occurrence.
   *
   * @param object the object to search for.
   * @return the index of the first occurrence of the object or -1 if the
   * object was not found.
   */
  public int getLocation(ImageInfo object) {
    return data.indexOf(object);
  }

  /**
   * Clear the entire adapter using {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRangeRemoved}.
   */
  public void clear() {
    int size = data.size();
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
   * @throws ClassCastException            if the class of the object is inappropriate for this
   *                                       data.
   * @throws IllegalArgumentException      if the object cannot be added to the data.
   */
  public boolean add(ImageInfo object) {
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
   * @return {@code true} if the data is modified, {@code false} otherwise
   * (i.e. if the passed collection was empty).
   * @throws UnsupportedOperationException if adding to the data is not supported.
   * @throws ClassCastException            if the class of an object is inappropriate for this
   *                                       data.
   * @throws IllegalArgumentException      if an object cannot be added to the data.
   */
  public boolean addAll(List<ImageInfo> collection) {
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
   * @param object   the object to add.
   * @throws UnsupportedOperationException if adding to the data is not supported.
   * @throws ClassCastException            if the class of the object is inappropriate for this
   *                                       data.
   * @throws IllegalArgumentException      if the object cannot be added to the data.
   * @throws IndexOutOfBoundsException     if {@code location < 0 || location > size()}
   */
  public void add(int location, ImageInfo object) {
    data.add(location, object);
    notifyItemInserted(location);
  }

  /**
   * Removes the first occurrence of the specified object from the data.
   *
   * @param object the object to remove.
   * @return true if the data was modified by this operation, false
   * otherwise.
   * @throws UnsupportedOperationException if removing from the data is not supported.
   */
  public boolean remove(int location, ImageInfo object) {
    final boolean removed = data.remove(object);
    notifyItemRangeRemoved(location, data.size());
    return removed;
  }

  /**
   * Removes the first occurrence of the specified object from the data.
   *
   * @param object the object to remove.
   * @return true if the data was modified by this operation, false
   * otherwise.
   * @throws UnsupportedOperationException if removing from the data is not supported.
   */
  public boolean remove(ImageInfo object) {
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
   * @throws IndexOutOfBoundsException     if {@code location < 0 || location >= size()}
   */
  public ImageInfo remove(int location) {
    final ImageInfo removedObject = data.remove(location);
    notifyItemRemoved(location);
    notifyItemRangeChanged(location, data.size());
    return removedObject;
  }
}

package burrows.apps.giphy.example.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Adapter for RecyclerViews.
 * <p>
 * This is based off of my open source project here:
 * <p>
 * https://github.com/jaredsburrows/android-gradle-java-app-template/blob/master/src/main/java/burrows/apps/example/template/adapter/BaseAdapter.java
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Data in the Adapter.
     */
    protected List<T> mModel = new ArrayList<>();

    /**
     * Returns the number of elements in the mModel.
     *
     * @return the number of elements in the mModel.
     */
    @Override
    public int getItemCount() {
        return this.mModel.size();
    }

    /**
     * Returns the instance of the mModel.
     *
     * @return instance of the mModel.
     */
    public List<T> getList() {
        return this.mModel;
    }

    /**
     * Returns the element at the specified location in the mModel.
     *
     * @param location the index of the element to return.
     * @return the element at the specified location.
     * @throws IndexOutOfBoundsException if {@code location < 0 || location >= size()}
     */
    public T getItem(final int location) {
        return this.mModel.get(location);
    }

    /**
     * Searches the mModel for the specified object and returns the index of the
     * first occurrence.
     *
     * @param object the object to search for.
     * @return the index of the first occurrence of the object or -1 if the
     * object was not found.
     */
    public int getLocation(final T object) {
        return this.mModel.indexOf(object);
    }

    /**
     * Clear the entire adapter using {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRangeRemoved}.
     */
    public void clear() {
        int size = this.mModel.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mModel.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * Adds the specified object at the end of the mModel.
     *
     * @param object the object to add.
     * @return always true.
     * @throws UnsupportedOperationException if adding to the mModel is not supported.
     * @throws ClassCastException            if the class of the object is inappropriate for this
     *                                       mModel.
     * @throws IllegalArgumentException      if the object cannot be added to the mModel.
     */
    public boolean add(final T object) {
        final boolean added = this.mModel.add(object);
        this.notifyItemInserted(this.mModel.size() + 1);
        return added;
    }

    /**
     * Adds the objects in the specified collection to the end of the mModel. The
     * objects are added in the order in which they are returned from the
     * collection's iterator.
     *
     * @param collection the collection of objects.
     * @return {@code true} if the mModel is modified, {@code false} otherwise
     * (i.e. if the passed collection was empty).
     * @throws UnsupportedOperationException if adding to the mModel is not supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       mModel.
     * @throws IllegalArgumentException      if an object cannot be added to the mModel.
     */
    public boolean addAll(final List<T> collection) {
        final boolean added = this.mModel.addAll(collection);
        this.notifyItemRangeInserted(0, mModel.size() + 1);
        return added;
    }

    /**
     * Inserts the specified object into the mModel at the specified location.
     * The object is inserted before the current element at the specified
     * location. If the location is equal to the size of the mModel, the object
     * is added at the end. If the location is smaller than the size of the
     * mModel, then all elements beyond the specified location are moved by one
     * location towards the end of the mModel.
     *
     * @param location the index at which to insert.
     * @param object   the object to add.
     * @throws UnsupportedOperationException if adding to the mModel is not supported.
     * @throws ClassCastException            if the class of the object is inappropriate for this
     *                                       mModel.
     * @throws IllegalArgumentException      if the object cannot be added to the mModel.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location > size()}
     */
    public void add(final int location, final T object) {
        this.mModel.add(location, object);
        this.notifyItemInserted(location);
    }

    /**
     * Removes the first occurrence of the specified object from the mModel.
     *
     * @param object the object to remove.
     * @return true if the mModel was modified by this operation, false
     * otherwise.
     * @throws UnsupportedOperationException if removing from the mModel is not supported.
     */
    public boolean remove(final int location, final T object) {
        final boolean removed = this.mModel.remove(object);
        this.notifyItemRangeRemoved(location, this.mModel.size());
        return removed;
    }

    /**
     * Removes the first occurrence of the specified object from the mModel.
     *
     * @param object the object to remove.
     * @return true if the mModel was modified by this operation, false
     * otherwise.
     * @throws UnsupportedOperationException if removing from the mModel is not supported.
     */
    public boolean remove(final T object) {
        final int location = this.getLocation(object);
        final boolean removed = this.mModel.remove(object);
        this.notifyItemRemoved(location);
        return removed;
    }

    /**
     * Removes the object at the specified location from the mModel.
     *
     * @param location the index of the object to remove.
     * @return the removed object.
     * @throws UnsupportedOperationException if removing from the mModel is not supported.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location >= size()}
     */
    public T remove(final int location) {
        final T removedObject = this.mModel.remove(location);
        this.notifyItemRemoved(location);
        this.notifyItemRangeChanged(location, this.mModel.size());
        return removedObject;
    }
}

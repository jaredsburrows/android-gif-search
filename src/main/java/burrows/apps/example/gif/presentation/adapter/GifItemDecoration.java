package burrows.apps.example.gif.presentation.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView ItemDecoration for custom space divider.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GifItemDecoration extends RecyclerView.ItemDecoration {
  private final int offSet;
  private final int columns;

  public GifItemDecoration(int offSet, int columns) {
    this.offSet = offSet;
    this.columns = columns;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);

    final int position = parent.getChildLayoutPosition(view);
    final int dataSize = state.getItemCount();

    // Apply inner right
    if (position % columns < columns - 1) outRect.right = offSet;

    // Apply inner left
    if (position % columns > 0) outRect.left = offSet;

    // Apply top padding
    if (position < columns) {
      outRect.bottom = 0; // Make the top of the RecyclerView have no padding
    } else {
      outRect.top = offSet;
    }

    // Apply bottom padding
    if (position >= dataSize || position >= (dataSize - columns)) {
      outRect.bottom = 0; // Make the bottom of the RecyclerView have no padding
    } else {
      outRect.bottom = offSet;
    }
  }
}

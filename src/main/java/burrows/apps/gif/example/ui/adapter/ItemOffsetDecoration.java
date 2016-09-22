package burrows.apps.gif.example.ui.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView ItemDecoration for custom space divider.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
  private int offSet;

  public ItemOffsetDecoration(final int offSet) {
    this.offSet = offSet;
  }

  public ItemOffsetDecoration(@NonNull final Context context, @DimenRes final int resourceId) {
    this(context.getResources().getDimensionPixelSize(resourceId));
  }

  @Override public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                                       final RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);

    outRect.set(offSet, offSet, offSet, offSet);
  }
}

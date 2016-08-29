package burrows.apps.giphy.example.ui.adapter;

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
    private int mOffSet;

    public ItemOffsetDecoration(final int offSet) {
        this.mOffSet = offSet;
    }

    public ItemOffsetDecoration(@NonNull final Context context, @DimenRes final int resourceId) {
        this(context.getResources().getDimensionPixelSize(resourceId));
    }

    @Override public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(this.mOffSet, this.mOffSet, this.mOffSet, this.mOffSet);
    }
}

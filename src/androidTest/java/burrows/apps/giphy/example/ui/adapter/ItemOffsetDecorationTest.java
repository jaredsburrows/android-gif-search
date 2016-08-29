package burrows.apps.giphy.example.ui.adapter;

import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import burrows.apps.giphy.example.R;
import burrows.apps.giphy.example.ui.activity.MainActivity;
import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import test.AndroidTestBase;

import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Ignore
public class ItemOffsetDecorationTest extends AndroidTestBase<MainActivity> {

    private final GridLayoutManager GRID_LAYOUT_MANAGER = new GridLayoutManager(mContext, 3);
    private final Rect RECT = new Rect();
    private final RecyclerView.State STATE = new RecyclerView.State();
    private RecyclerView mRecyclerView;
    private ItemOffsetDecoration sut;

    public ItemOffsetDecorationTest() {
        super(MainActivity.class);
    }

    @Before @Override public void setUp() throws Exception {
        super.setUp();

        final GiphyAdapter adapter = new GiphyAdapter();
        this.mRecyclerView = new RecyclerView(mContext);

        // Setup RecyclerView
        this.mRecyclerView.setAdapter(adapter);
        this.mRecyclerView.setLayoutManager(GRID_LAYOUT_MANAGER);

        // Add fake data
        adapter.add(new GiphyImageInfo());

        // Increase the childcount
        this.mRecyclerView.addView(new AppCompatTextView(mContext));

        final RecyclerView.LayoutParams layoutParams = Mockito.mock(RecyclerView.LayoutParams.class);
        when(layoutParams.getViewLayoutPosition()).thenReturn(0);
        this.mRecyclerView.setLayoutParams(layoutParams);
    }

    @Test public void testGetItemOffsetsInt() {
        // Item decoration with specified offset
        sut = new ItemOffsetDecoration(1);
        this.mRecyclerView.addItemDecoration(sut);

        sut.getItemOffsets(RECT, this.mRecyclerView, this.mRecyclerView, STATE);
    }

    @Test public void testGetItemOffsetsContextResId() {
        // Item decoration with specified context and dimen
        sut = new ItemOffsetDecoration(mContext, R.dimen.gif_adapter_item_offset);
        this.mRecyclerView.addItemDecoration(sut);

        sut.getItemOffsets(RECT, this.mRecyclerView, this.mRecyclerView, STATE);
    }
}

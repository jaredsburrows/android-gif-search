package burrows.apps.gif.example.ui.adapter;

import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import burrows.apps.gif.example.R;
import burrows.apps.gif.example.ui.adapter.model.ImageInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import test.RoboTestBase;

import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ItemOffsetDecorationTest extends RoboTestBase {
  private final GridLayoutManager GRID_LAYOUT_MANAGER = new GridLayoutManager(CONTEXT, 3);
  private final Rect RECT = new Rect();
  private final RecyclerView.State STATE = new RecyclerView.State();
  private RecyclerView recyclerView;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    final GifAdapter adapter = new GifAdapter();
    recyclerView = new RecyclerView(CONTEXT);

    // Setup RecyclerView
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(GRID_LAYOUT_MANAGER);

    // Add fake data
    adapter.add(new ImageInfo());

    // Increase the childcount
    recyclerView.addView(new AppCompatTextView(CONTEXT));

    final RecyclerView.LayoutParams layoutParams = Mockito.mock(RecyclerView.LayoutParams.class);
    when(layoutParams.getViewLayoutPosition()).thenReturn(0);
    recyclerView.setLayoutParams(layoutParams);
  }

  @Test public void testGetItemOffsetsInt() {
    // Item decoration with specified offset
    final ItemOffsetDecoration sut = new ItemOffsetDecoration(1);
    recyclerView.addItemDecoration(sut);

    sut.getItemOffsets(RECT, recyclerView, recyclerView, STATE);
  }

  @Test public void testGetItemOffsetsContextResId() {
    // Item decoration with specified context and dimen
    final ItemOffsetDecoration sut = new ItemOffsetDecoration(CONTEXT, R.dimen.gif_adapter_item_offset);
    recyclerView.addItemDecoration(sut);

    sut.getItemOffsets(RECT, recyclerView, recyclerView, STATE);
  }
}

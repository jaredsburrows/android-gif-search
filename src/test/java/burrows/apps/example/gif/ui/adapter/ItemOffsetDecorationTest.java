package burrows.apps.example.gif.ui.adapter;

import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import burrows.apps.example.gif.ui.adapter.model.ImageInfo;
import org.junit.Before;
import org.junit.Test;
import test.RoboTestBase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ItemOffsetDecorationTest extends RoboTestBase {
  private final GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
  private final Rect rect = new Rect();
  private final RecyclerView.State state = new RecyclerView.State();
  private RecyclerView recyclerView;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    final GifAdapter adapter = new GifAdapter(application);
    recyclerView = new RecyclerView(context);

    // Setup RecyclerView
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);

    // Add fake data
    adapter.add(new ImageInfo());

    // Increase the childcount
    recyclerView.addView(new AppCompatTextView(context));

    final RecyclerView.LayoutParams layoutParams = mock(RecyclerView.LayoutParams.class);
    when(layoutParams.getViewLayoutPosition()).thenReturn(0);
    recyclerView.setLayoutParams(layoutParams);
  }

  @Test public void testGetItemOffsetsContextResId() {
    // Item decoration with specified context and dimen
    final GifItemDecoration sut = new GifItemDecoration(context, layoutManager.getSpanCount());
    recyclerView.addItemDecoration(sut);

    sut.getItemOffsets(rect, recyclerView, recyclerView, state);
  }
}

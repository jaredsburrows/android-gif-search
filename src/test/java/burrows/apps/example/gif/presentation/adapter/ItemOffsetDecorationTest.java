package burrows.apps.example.gif.presentation.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import test.RoboTestBase;
import test.TestBase;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ItemOffsetDecorationTest extends RoboTestBase {
  @Mock private Rect rect;
  @Mock private RecyclerView.State state;
  @Mock GifAdapter.OnItemClickListener onItemClickListener;
  @Mock ImageRepository imageDownloader;
  @Mock RecyclerView.LayoutParams layoutParams;
  private GridLayoutManager layoutManager;
  private RecyclerView recyclerView;
  private GifItemDecoration sut;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    initMocks(this);

    layoutManager = new GridLayoutManager(context, 3);

    final GifAdapter adapter = new GifAdapter(onItemClickListener, imageDownloader);
    recyclerView = new RecyclerView(context);

    // Setup RecyclerView
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);

    // Add fake data
    adapter.add(new ImageInfo());

    // Increase the childcount
    recyclerView.addView(new AppCompatTextView(context));

    when(layoutParams.getViewLayoutPosition()).thenReturn(0);
    recyclerView.setLayoutParams(layoutParams);
  }

  @Test public void testGetItemOffsetsContextResId() {
    // Item decoration with specified context and dimen
    sut = new GifItemDecoration(context, layoutManager.getSpanCount());
    recyclerView.addItemDecoration(sut);

    sut.getItemOffsets(rect, recyclerView, recyclerView, state);
  }
}

package burrows.apps.example.gif.presentation.adapter;

import android.graphics.Rect;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import burrows.apps.example.gif.DummyActivity;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import test.AndroidTestBase;
import test.CustomTestRule;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public final class GifItemDecorationTest extends AndroidTestBase {
  @Rule public final CustomTestRule<DummyActivity> activityTestRule = new CustomTestRule<>(DummyActivity.class, true, true);
  @Mock private RecyclerView.State state;
  @Mock GifAdapter.OnItemClickListener onItemClickListener;
  @Mock ImageRepository imageDownloader;
  @Mock RecyclerView.LayoutParams layoutParams;
  private final Rect rect = new Rect(0, 0, 0, 0);
  private GridLayoutManager layoutManager;
  private RecyclerView recyclerView;
  private GifItemDecoration sut;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    initMocks(this);

    layoutManager = new GridLayoutManager(activityTestRule.getTargetContext(), 3);

    final GifAdapter adapter = new GifAdapter(onItemClickListener, imageDownloader);
    recyclerView = new RecyclerView(activityTestRule.getTargetContext());

    // Setup RecyclerView
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);

    // Add fake data
    for (int i = 0; i < RiffsyRepository.DEFAULT_LIMIT_COUNT; i++) {
      adapter.add(new ImageInfoModel());
    }

    // Increase the childcount
    recyclerView.addView(new AppCompatTextView(activityTestRule.getTargetContext()));

    when(layoutParams.getViewLayoutPosition()).thenReturn(0);
    recyclerView.setLayoutParams(layoutParams);
  }

  @Test public void testGetItemOffsetsContextResId() {
    // Item decoration with specified context and dimen
    sut = new GifItemDecoration(INTEGER_RANDOM, layoutManager.getSpanCount());
    recyclerView.addItemDecoration(sut);

    sut.getItemOffsets(rect, recyclerView, recyclerView, state);
  }
}

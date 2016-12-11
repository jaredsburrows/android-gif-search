package burrows.apps.example.gif.presentation.adapter;

import android.support.test.filters.SmallTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.presentation.adapter.GifAdapter.OnItemClickListener;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel;
import burrows.apps.example.gif.presentation.main.MainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import test.AndroidTestBase;
import test.CustomTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public final class GifAdapterTest extends AndroidTestBase {
  @Rule public final CustomTestRule<MainActivity> activityTestRule = new CustomTestRule<>(MainActivity.class, true, true);
  @Rule public final UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();
  @Mock OnItemClickListener onItemClickListener;
  private final ImageInfoModel imageInfoModel = new ImageInfoModel.Builder().url(STRING_UNIQUE).build();
  private final ImageInfoModel imageInfoModel2 = new ImageInfoModel.Builder().url(STRING_UNIQUE2).build();
  private final ImageInfoModel imageInfoModel3 = new ImageInfoModel.Builder().url(STRING_UNIQUE3).build();
  GifAdapter.ViewHolder viewHolder;
  ImageRepository spyImageDownloader;
  GifAdapter sut;

  @Before @Override public void setUp() throws Exception {
    activityTestRule.keepScreenOn();

    initMocks(this);

    spyImageDownloader = spy(new ImageRepository(activityTestRule.getTargetContext()));
    sut = new GifAdapter(onItemClickListener, spyImageDownloader);
    sut.add(imageInfoModel);
    sut.add(imageInfoModel2);
    viewHolder = sut.onCreateViewHolder(new LinearLayout(activityTestRule.getTargetContext()), 0);
  }

  @After @Override public void tearDown() throws Exception {
    sut.clear();
  }

  @Test public void testOnCreateViewHolder() throws Exception {
    final ViewGroup parent = new ViewGroup(activityTestRule.getTargetContext()) {
      @Override protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
      }
    };

    assertThat(sut.onCreateViewHolder(parent, 0)).isInstanceOf(GifAdapter.ViewHolder.class);
  }

  @Test public void testOnBindViewHolderOnAdapterItemClick() throws Throwable {
    sut.clear();

    // must have one
    sut.add(imageInfoModel);
    sut.add(imageInfoModel2);
    sut.add(new ImageInfoModel());

    uiThreadTestRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        sut.onBindViewHolder(viewHolder, 0);
      }
    });

    assertThat(viewHolder.itemView.performClick()).isTrue();

    verify(spyImageDownloader, atLeastOnce()).load(any());
    verify(onItemClickListener).onClick(any());
  }

  @Test public void testGetItem() {
    sut.clear();

    final ImageInfoModel imageInfo = new ImageInfoModel();
    sut.add(imageInfo);

    assertThat(sut.getItem(0)).isEqualTo(imageInfo);
  }

  @Test public void onViewRecycled() throws Throwable {
    sut.add(new ImageInfoModel());

    uiThreadTestRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        sut.onBindViewHolder(viewHolder, 0);
        sut.onViewRecycled(viewHolder);
      }
    });
  }

  @Test public void testGetItemCountShouldReturnCorrectValues() {
    assertThat(sut.getItemCount()).isEqualTo(2);
  }

  @Test public void testGetListCountShouldReturnCorrectValues() {
    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfoModel, imageInfoModel2));
  }

  @Test public void testGetItemShouldReturnCorrectValues() {
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2);
  }

  @Test public void testGetLocationShouldReturnCorrectValues() {
    assertThat(sut.getLocation(imageInfoModel2)).isEqualTo(1);
  }

  @Test public void testClearShouldClearAdapter() {
    sut.clear();

    assertThat(sut.getItemCount()).isEqualTo(0);
  }

  @Test public void testAddObjectShouldReturnCorrectValues() {
    sut.add(imageInfoModel3);

    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfoModel, imageInfoModel2, imageInfoModel3));
  }

  @Test public void testAddCollectionShouldReturnCorrectValues() {
    final List<ImageInfoModel> imageInfos = Collections.singletonList(imageInfoModel3);

    sut.addAll(imageInfos);

    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfoModel, imageInfoModel2, imageInfoModel3));
  }

  @Test public void testAddLocationObjectShouldReturnCorrectValues() {
    sut.add(0, new ImageInfoModel.Builder().url(STRING_UNIQUE3).build());

    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfoModel3, imageInfoModel, imageInfoModel2));
  }

  @Test public void testRemoveLocationObjectShouldReturnCorrectValues() {
    sut.remove(0, imageInfoModel);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(imageInfoModel2));
  }

  @Test public void testRemoveObjectShouldReturnCorrectValues() {
    sut.remove(imageInfoModel);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(imageInfoModel2));
  }

  @Test public void testRemoveLocationShouldReturnCorrectValues() {
    sut.remove(0);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(imageInfoModel2));
  }
}

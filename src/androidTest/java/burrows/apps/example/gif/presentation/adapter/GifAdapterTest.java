package burrows.apps.example.gif.presentation.adapter;

import android.support.test.filters.SmallTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import burrows.apps.example.gif.DummyActivity;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.presentation.adapter.GifAdapter.OnItemClickListener;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import test.AndroidTestBase;
import test.CustomTestRule;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SmallTest @RunWith(AndroidJUnit4.class)
public final class GifAdapterTest extends AndroidTestBase {
  @Rule public final CustomTestRule<DummyActivity> activityTestRule = new CustomTestRule<>(DummyActivity.class, true, true);
  @Rule public final UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();
  @Mock OnItemClickListener onItemClickListener;
  private final ImageInfoModel imageInfoModel = new ImageInfoModel.Builder().url(STRING_UNIQUE).build();
  private final ImageInfoModel imageInfoModel2 = new ImageInfoModel.Builder().url(STRING_UNIQUE2).build();
  private final ImageInfoModel imageInfoModel3 = new ImageInfoModel.Builder().url(STRING_UNIQUE3).build();
  GifAdapter.ViewHolder viewHolder;
  ImageRepository spyImageDownloader;
  GifAdapter sut;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    activityTestRule.keepScreenOn();

    initMocks(this);

    spyImageDownloader = spy(new ImageRepository(activityTestRule.getTargetContext()));
    sut = new GifAdapter(onItemClickListener, spyImageDownloader);
    sut.add(imageInfoModel);
    sut.add(imageInfoModel2);
    viewHolder = sut.onCreateViewHolder(new LinearLayout(activityTestRule.getTargetContext()), 0);
  }

  @Test public void testOnCreateViewHolder() throws Exception {
    // Arrange
    final ViewGroup parent = new ViewGroup(activityTestRule.getTargetContext()) {
      @Override protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
      }
    };

    // Assert
    assertThat(sut.onCreateViewHolder(parent, 0)).isInstanceOf(GifAdapter.ViewHolder.class);
  }

  @Test public void testOnBindViewHolderOnAdapterItemClick() throws Throwable {
    // Arrange
    sut.clear();
    sut.add(imageInfoModel);
    sut.add(imageInfoModel2);
    sut.add(new ImageInfoModel());

    // Act
    uiThreadTestRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        sut.onBindViewHolder(viewHolder, 0);
      }
    });

    // Assert
    assertThat(viewHolder.itemView.performClick()).isTrue();
    verify(spyImageDownloader, atLeastOnce()).load(eq(STRING_UNIQUE));
    verify(onItemClickListener).onClick(eq(imageInfoModel));
  }

  @Test public void testGetItem() {
    // Arrange
    sut.clear();

    // Act
    final ImageInfoModel imageInfo = new ImageInfoModel();
    sut.add(imageInfo);

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfo);
  }

  @Test public void onViewRecycled() throws Throwable {
    // Arrange
    sut.add(new ImageInfoModel());

    // Act
    uiThreadTestRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        sut.onBindViewHolder(viewHolder, 0);
        sut.onViewRecycled(viewHolder);
      }
    });
  }

  @Test public void testGetItemCountShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getItemCount()).isEqualTo(2);
  }

  @Test public void testGetListCountShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel);
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2);
  }

  @Test public void testGetItemShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2);
  }

  @Test public void testGetLocationShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getLocation(imageInfoModel2)).isEqualTo(1);
  }

  @Test public void testClearShouldClearAdapter() {
    sut.clear();

    // Assert
    assertThat(sut.getItemCount()).isEqualTo(0);
  }

  @Test public void testAddObjectShouldReturnCorrectValues() {
    sut.add(imageInfoModel3);

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel);
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2);
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel3);
  }

  @Test public void testAddCollectionShouldReturnCorrectValues() {
    final List<ImageInfoModel> imageInfos = Collections.singletonList(imageInfoModel3);

    sut.addAll(imageInfos);

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel);
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2);
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel3);
  }

  @Test public void testAddLocationObjectShouldReturnCorrectValues() {
    sut.add(0, new ImageInfoModel.Builder().url(STRING_UNIQUE3).build());

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel3);
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel);
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel2);
  }

  @Test public void testRemoveLocationObjectShouldReturnCorrectValues() {
    sut.remove(0, imageInfoModel);

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel2);
  }

  @Test public void testRemoveObjectShouldReturnCorrectValues() {
    sut.remove(imageInfoModel);

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel2);
  }

  @Test public void testRemoveLocationShouldReturnCorrectValues() {
    sut.remove(0);

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel2);
  }
}

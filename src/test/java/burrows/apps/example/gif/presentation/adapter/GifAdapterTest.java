package burrows.apps.example.gif.presentation.adapter;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfo;
import com.bumptech.glide.GifRequestBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import test.RoboTestBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class GifAdapterTest extends RoboTestBase {
  private final ImageInfo imageInfo = new ImageInfo.Builder().url(STRING_UNIQUE).build();
  private final ImageInfo imageInfo2 = new ImageInfo.Builder().url(STRING_UNIQUE2).build();
  private final ImageInfo imageInfo3 = new ImageInfo.Builder().url(STRING_UNIQUE3).build();
  private GifAdapter.ViewHolder viewHolder;
  @Mock GifAdapter.OnItemClickListener onItemClickListener;
  @Mock GifRequestBuilder requestBuilder;
  ImageRepository spyImageDownloader;
  private GifAdapter sut;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    initMocks(this);

    spyImageDownloader = spy(new ImageRepository(context));
    sut = new GifAdapter(onItemClickListener, spyImageDownloader);
    sut.add(imageInfo);
    sut.add(imageInfo2);
    viewHolder = sut.onCreateViewHolder(new LinearLayout(context), 0);
  }

  @After @Override public void tearDown() throws Exception {
    super.tearDown();

    sut.clear();
  }

  @Test public void testOnCreateViewHolder() throws Exception {
    final ViewGroup parent = new ViewGroup(context) {
      @Override protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
      }
    };

    assertThat(sut.onCreateViewHolder(parent, 0)).isInstanceOf(GifAdapter.ViewHolder.class);
  }

  @Test public void testOnBindViewHolderOnAdapterItemClick() {
    sut.clear();

    // must have one
    sut.add(imageInfo);
    sut.add(imageInfo2);
    sut.add(new ImageInfo());

    sut.onBindViewHolder(viewHolder, 0);

    assertThat(viewHolder.itemView.performClick()).isTrue();

    verify(spyImageDownloader, atLeastOnce()).load(any());
    verify(onItemClickListener).onClick(any());
  }

  @Test public void testGetItem() {
    sut.clear();

    final ImageInfo imageInfo = new ImageInfo();
    sut.add(imageInfo);

    assertThat(sut.getItem(0)).isEqualTo(imageInfo);
  }

  @Test public void onViewRecycled() throws Exception {
    sut.add(new ImageInfo());
    sut.onBindViewHolder(viewHolder, 0);

    sut.onViewRecycled(viewHolder);
  }

  @Test public void testGetItemCountShouldReturnCorrectValues() {
    assertThat(sut.getItemCount()).isEqualTo(2);
  }

  @Test public void testGetListCountShouldReturnCorrectValues() {
    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfo, imageInfo2));
  }

  @Test public void testGetItemShouldReturnCorrectValues() {
    assertThat(sut.getItem(1)).isEqualTo(imageInfo2);
  }

  @Test public void testGetLocationShouldReturnCorrectValues() {
    assertThat(sut.getLocation(imageInfo2)).isEqualTo(1);
  }

  @Test public void testClearShouldClearAdapter() {
    sut.clear();

    assertThat(sut.getItemCount()).isEqualTo(0);
  }

  @Test public void testAddObjectShouldReturnCorrectValues() {
    sut.add(imageInfo3);

    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfo, imageInfo2, imageInfo3));
  }

  @Test public void testAddCollectionShouldReturnCorrectValues() {
    final List<ImageInfo> imageInfos = Collections.singletonList(imageInfo3);

    sut.addAll(imageInfos);

    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfo, imageInfo2, imageInfo3));
  }

  @Test public void testAddLocationObjectShouldReturnCorrectValues() {
    sut.add(0, new ImageInfo.Builder().url(STRING_UNIQUE3).build());

    assertThat(sut.getList()).isEqualTo(Arrays.asList(imageInfo3, imageInfo, imageInfo2));
  }

  @Test public void testRemoveLocationObjectShouldReturnCorrectValues() {
    sut.remove(0, imageInfo);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(imageInfo2));
  }

  @Test public void testRemoveObjectShouldReturnCorrectValues() {
    sut.remove(imageInfo);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(imageInfo2));
  }

  @Test public void testRemoveLocationShouldReturnCorrectValues() {
    sut.remove(0);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(imageInfo2));
  }
}

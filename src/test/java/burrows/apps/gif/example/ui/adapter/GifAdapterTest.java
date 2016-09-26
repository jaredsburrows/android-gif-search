package burrows.apps.gif.example.ui.adapter;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import burrows.apps.gif.example.ui.adapter.model.ImageInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.RoboTestBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GifAdapterTest extends RoboTestBase {
  private GifAdapter.GifAdapterViewHolder viewHolder;
  private GifAdapter sut;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    sut = new GifAdapter(application);
    sut.add(new ImageInfo().withUrl(STRING_UNIQUE));
    sut.add(new ImageInfo().withUrl(STRING_UNIQUE2));
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

    final GifAdapter.GifAdapterViewHolder viewHolder = sut.onCreateViewHolder(parent, 0);

    assertThat(viewHolder).isInstanceOf(GifAdapter.GifAdapterViewHolder.class);
  }

  @Test public void testOnBindViewHolderOnAdapterItemClick() {
    sut.clear();

    // must have one
    sut.add(new ImageInfo().withUrl(STRING_UNIQUE));
    sut.add(new ImageInfo().withUrl(STRING_UNIQUE2));
    sut.add(new ImageInfo());

    sut.onBindViewHolder(viewHolder, 0);

    assertThat(viewHolder.itemView.performClick()).isTrue();
  }

  @Test public void testGetItem() {
    sut.clear();

    final ImageInfo model = new ImageInfo();
    sut.add(model);

    assertThat(sut.getItem(0)).isEqualTo(model);
  }

  @Test public void onViewRecycled() throws Exception {
    sut.add(new ImageInfo());
    sut.onBindViewHolder(viewHolder, 0);

    sut.onViewRecycled(viewHolder);
  }


  // --------------------------------------------
  // getItemCount()
  // --------------------------------------------

  @Test public void testGetItemCountShouldReturnCorrectValues() {
    assertThat(sut.getItemCount()).isEqualTo(2);
  }

  // --------------------------------------------
  // getList()
  // --------------------------------------------

  @Test public void testGetListCountShouldReturnCorrectValues() {
    assertThat(sut.getList()).isEqualTo(Arrays.asList(new ImageInfo().withUrl(STRING_UNIQUE),
      new ImageInfo().withUrl(STRING_UNIQUE2)));
  }

  // --------------------------------------------
  // getItem(final int location)
  // --------------------------------------------

  @Test public void testGetItemShouldReturnCorrectValues() {
    assertThat(sut.getItem(1)).isEqualTo(new ImageInfo().withUrl(STRING_UNIQUE2));
  }

  // --------------------------------------------
  // getLocation(final T object)
  // --------------------------------------------

  @Test public void testGetLocationShouldReturnCorrectValues() {
    assertThat(sut.getLocation(new ImageInfo().withUrl(STRING_UNIQUE2))).isEqualTo(1);
  }

  // --------------------------------------------
  // clear()
  // --------------------------------------------

  @Test public void testClearShouldClearAdapter() {
    sut.clear();

    assertThat(sut.getItemCount()).isEqualTo(0);
  }

  // --------------------------------------------
  // add(final T object)
  // --------------------------------------------

  @Test public void testAddObjectShouldReturnCorrectValues() {
    sut.add(new ImageInfo().withUrl(STRING_UNIQUE3));

    assertThat(sut.getList()).isEqualTo(Arrays.asList(new ImageInfo().withUrl(STRING_UNIQUE),
      new ImageInfo().withUrl(STRING_UNIQUE2),
      new ImageInfo().withUrl(STRING_UNIQUE3)));
  }

  // --------------------------------------------
  // add(final List<T> collection)
  // --------------------------------------------

  @Test public void testAddCollectionShouldReturnCorrectValues() {
    final List<ImageInfo> list = Collections.singletonList(new ImageInfo().withUrl(STRING_UNIQUE3));

    sut.addAll(list);

    assertThat(sut.getList()).isEqualTo(Arrays.asList(new ImageInfo().withUrl(STRING_UNIQUE),
      new ImageInfo().withUrl(STRING_UNIQUE2),
      new ImageInfo().withUrl(STRING_UNIQUE3)));
  }

  // --------------------------------------------
  // add(final int location, final T object)
  // --------------------------------------------

  @Test public void testAddLocationObjectShouldReturnCorrectValues() {
    sut.add(0, new ImageInfo().withUrl(STRING_UNIQUE3));

    assertThat(sut.getList()).isEqualTo(Arrays.asList(new ImageInfo().withUrl(STRING_UNIQUE3),
      new ImageInfo().withUrl(STRING_UNIQUE),
      new ImageInfo().withUrl(STRING_UNIQUE2)));
  }

  // --------------------------------------------
  // remove(final int location, final T object)
  // --------------------------------------------

  @Test public void testRemoveLocationObjectShouldReturnCorrectValues() {
    sut.remove(0, new ImageInfo().withUrl(STRING_UNIQUE));

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(new ImageInfo().withUrl(STRING_UNIQUE2)));
  }

  // --------------------------------------------
  // remove(final T object)
  // --------------------------------------------

  @Test public void testRemoveObjectShouldReturnCorrectValues() {
    sut.remove(new ImageInfo().withUrl(STRING_UNIQUE));

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(new ImageInfo().withUrl(STRING_UNIQUE2)));
  }

  // --------------------------------------------
  // remove(final int location)
  // --------------------------------------------

  @Test public void testRemoveLocationShouldReturnCorrectValues() {
    sut.remove(0);

    assertThat(sut.getList()).isEqualTo(Collections.singletonList(new ImageInfo().withUrl(STRING_UNIQUE2)));
  }
}

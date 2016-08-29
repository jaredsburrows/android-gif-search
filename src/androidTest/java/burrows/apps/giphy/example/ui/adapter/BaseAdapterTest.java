package burrows.apps.giphy.example.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import burrows.apps.giphy.example.ui.activity.MainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import test.AndroidTestBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Ignore
public final class BaseAdapterTest extends AndroidTestBase<MainActivity> {
    private BaseAdapter<String, TestAdapter.TestViewHolder> sut;

    public BaseAdapterTest() {
        super(MainActivity.class);
    }

    @Before @Override public void setUp() throws Exception {
        super.setUp();

        sut = new TestAdapter(); // use String, has equals/compareTo

        // mModel
        sut.add(STRING_UNIQUE);
        sut.add(STRING_UNIQUE2);
    }

    @After @Override public void tearDown() throws Exception {
        super.tearDown();

        sut.clear();
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
        assertThat(sut.getList()).isEqualTo(Arrays.asList(STRING_UNIQUE, STRING_UNIQUE2));
    }

    // --------------------------------------------
    // getItem(final int location)
    // --------------------------------------------

    @Test public void testGetItemShouldReturnCorrectValues() {
        assertThat(sut.getItem(1)).isEqualTo(STRING_UNIQUE2);
    }

    // --------------------------------------------
    // getLocation(final T object)
    // --------------------------------------------

    @Test public void testGetLocationShouldReturnCorrectValues() {
        assertThat(sut.getLocation(STRING_UNIQUE2)).isEqualTo(1);
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

    @Test
    public void testAddObjectShouldReturnCorrectValues() {
        sut.add(STRING_UNIQUE3);

        assertThat(sut.getList()).isEqualTo(Arrays.asList(STRING_UNIQUE, STRING_UNIQUE2, STRING_UNIQUE3));
    }

    // --------------------------------------------
    // add(final List<T> collection)
    // --------------------------------------------

    @Test public void testAddCollectionShouldReturnCorrectValues() {
        final List<String> list = Collections.singletonList(STRING_UNIQUE3);

        sut.addAll(list);

        assertThat(sut.getList()).isEqualTo(Arrays.asList(STRING_UNIQUE, STRING_UNIQUE2, STRING_UNIQUE3));
    }

    // --------------------------------------------
    // add(final int location, final T object)
    // --------------------------------------------

    @Test public void testAddLocationObjectShouldReturnCorrectValues() {
        sut.add(0, STRING_UNIQUE3);

        assertThat(sut.getList()).isEqualTo(Arrays.asList(STRING_UNIQUE3, STRING_UNIQUE, STRING_UNIQUE2));
    }

    // --------------------------------------------
    // remove(final int location, final T object)
    // --------------------------------------------

    @Test public void testRemoveLocationObjectShouldReturnCorrectValues() {
        sut.remove(0, STRING_UNIQUE);

        assertThat(sut.getList()).isEqualTo(Collections.singletonList(STRING_UNIQUE2));
    }

    // --------------------------------------------
    // remove(final T object)
    // --------------------------------------------

    @Test public void testRemoveObjectShouldReturnCorrectValues() {
        sut.remove(STRING_UNIQUE);

        assertThat(sut.getList()).isEqualTo(Collections.singletonList(STRING_UNIQUE2));
    }

    // --------------------------------------------
    // remove(final int location)
    // --------------------------------------------

    @Test public void testRemoveLocationShouldReturnCorrectValues() {
        sut.remove(0);

        assertThat(sut.getList()).isEqualTo(Collections.singletonList(STRING_UNIQUE2));
    }

    // stub
    public static class TestAdapter extends BaseAdapter<String, TestAdapter.TestViewHolder> {
        @Override
        public TestViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(final TestViewHolder testViewHolder, final int i) {
            // nothing
        }

        public static class TestViewHolder extends RecyclerView.ViewHolder {
            public TestViewHolder(final View itemView) {
                super(itemView);
            }
        }
    }
}

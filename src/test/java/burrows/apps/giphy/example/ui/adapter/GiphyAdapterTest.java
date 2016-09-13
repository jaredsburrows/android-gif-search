package burrows.apps.giphy.example.ui.adapter;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import org.junit.Before;
import org.junit.Test;
import test.RoboTestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class GiphyAdapterTest extends RoboTestBase {
    private GiphyAdapter.GiphyAdapterViewHolder mViewHolder;
    private GiphyAdapter sut;

    @Before @Override public void setUp() throws Exception {
        super.setUp();

        this.sut = new GiphyAdapter();
        this.mViewHolder = this.sut.onCreateViewHolder(new LinearLayout(CONTEXT), 0);
    }

    @Test public void testOnCreateViewHolder() throws Exception {
        final ViewGroup parent = new ViewGroup(CONTEXT) {
            @Override protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
            }
        };

        final GiphyAdapter.GiphyAdapterViewHolder viewHolder = this.sut.onCreateViewHolder(parent, 0);

        assertThat(viewHolder).isInstanceOf(GiphyAdapter.GiphyAdapterViewHolder.class);
    }

    @Test public void testOnBindViewHolderOnAdapterItemClick() {
        // must have one
        this.sut.add(new GiphyImageInfo().withUrl(STRING_UNIQUE));
        this.sut.add(new GiphyImageInfo().withUrl(STRING_UNIQUE2));
        this.sut.add(new GiphyImageInfo());

        this.sut.onBindViewHolder(this.mViewHolder, 0);

        assertThat(this.mViewHolder.itemView.performClick()).isTrue();
    }

    @Test public void testGetItem() {
        final GiphyImageInfo model = new GiphyImageInfo();
        this.sut.add(model);

        assertThat(this.sut.getItem(0)).isEqualTo(model);
    }

    @Test public void onViewRecycled() throws Exception {
        this.sut.add(new GiphyImageInfo());
        this.sut.onBindViewHolder(this.mViewHolder, 0);

        this.sut.onViewRecycled(this.mViewHolder);
    }
}

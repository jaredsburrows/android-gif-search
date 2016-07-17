package burrows.apps.giphy.example.ui.fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import burrows.apps.giphy.example.R;
import burrows.apps.giphy.example.ui.activity.MainActivity;
import burrows.apps.giphy.example.ui.adapter.GiphyAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;
import test.RoboTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.buildActivity;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class MainFragmentTest extends RoboTestBase {

    private ActivityController<MainActivity> mController;
    private FragmentActivity mActivity;
    private MainFragment sut;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.sut = new MainFragment();

        // Control the activity to control the fragment
        this.mController = buildActivity(MainActivity.class);

        this.mActivity = this.mController.create().start().resume().visible().get();
        this.mActivity.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, this.sut, null).commit();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        this.mController.pause().stop().destroy();
        this.mActivity.finish();
    }

    @Test
    public void testSearchView() {
        final GiphyAdapter adapter = ((GiphyAdapter) ((RecyclerView) this.sut.getView().findViewById(R.id.recyclerview_root)).getAdapter());
        final ShadowActivity shadowActivity = Shadows.shadowOf(this.sut.getActivity());
        final MenuItem menuItem = shadowActivity.getOptionsMenu().getItem(0);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        this.finishThreads();

        shadowActivity.clickMenuItem(R.id.menu_search);

        assertThat(searchView.isIconified()).isTrue();

        searchView.setQuery("test", true);

        this.finishThreads();

        shadowActivity.clickMenuItem(R.id.menu_search);

        assertThat(adapter.getItemCount()).isBetween(0, 24);
    }
}

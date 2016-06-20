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

    private ActivityController<MainActivity> controller;
    private MainFragment sut;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.sut = new MainFragment();

        // Control the activity to control the fragment
        this.controller = buildActivity(MainActivity.class);

        final FragmentActivity activity = this.controller.create().start().resume().visible().get();
        activity.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, this.sut, null).commit();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        this.controller.pause().stop().destroy();
    }

    @Test
    public void testSearchView() {
        final GiphyAdapter adapter = ((GiphyAdapter) ((RecyclerView) this.sut.getView().findViewById(R.id.recyclerview_root)).getAdapter());
        final ShadowActivity shadowActivity = Shadows.shadowOf(this.sut.getActivity());
        final MenuItem menuItem = shadowActivity.getOptionsMenu().getItem(0);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        shadowActivity.clickMenuItem(R.id.menu_search);

        assertThat(searchView.isIconified()).isTrue();

        searchView.setQuery("test", true);

        shadowActivity.clickMenuItem(R.id.menu_search);

        this.finishThreads();

        assertThat(adapter.getItemCount()).isBetween(0, 24);
    }
}

package burrows.apps.giphy.example.ui.activity;

import android.graphics.drawable.BitmapDrawable;
import burrows.apps.giphy.example.ui.fragment.MainFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.ActivityController;
import test.RoboTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.buildActivity;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class MainActivityTest extends RoboTestBase {

    private ActivityController<MainActivity> mController;
    private MainActivity sut;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Create new activity
        this.mController = buildActivity(MainActivity.class);
        this.sut = this.mController.create().postCreate(null).start().resume().visible().get();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        // Destroy activity
        this.mController.pause().stop().destroy();
        this.sut.finish();
    }

    // --------------------------------------------
    // Activity Life Cycle States
    // http://developer.android.com/reference/android/app/Activity.html
    // --------------------------------------------

    @Test
    public void testActivityShouldReturnToActivity() {
        this.mController = buildActivity(MainActivity.class);
        this.sut = this.mController.create().start().resume().visible().get();

        this.mController.pause().resume().visible().get();
        assertThat(this.sut).isNotNull();
    }

    @Test
    public void testActivityShouldNavigateToActivity() {
        this.mController = buildActivity(MainActivity.class);
        this.sut = this.mController.create().start().resume().visible().get();

        this.mController.pause().stop().restart().start().resume().visible().get();
        assertThat(this.sut).isNotNull();
    }

    @Test
    public void testActivityHasLoadedFragments() {
        assertThat(this.sut.getSupportFragmentManager().findFragmentByTag(MainFragment.class.getSimpleName())).isInstanceOf(MainFragment.class);
    }

    @Test
    public void testActivityHasLoadedToolbarTitle() {
        assertThat(this.sut.mToolbar.getTitle()).isEqualTo("Giphy: Top Trending Gifs");
    }

    @Test
    public void testActivityHasLoadedToolbarIcon() {
        assertThat(this.sut.mToolbar.getNavigationIcon()).isInstanceOf(BitmapDrawable.class);
    }
}

package burrows.apps.giphy.example.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import burrows.apps.giphy.example.R;
import burrows.apps.giphy.example.ui.fragment.MainFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainActivity extends AppCompatActivity {
    @BindView(R.id.tool_bar) Toolbar mToolbar;

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        // Setup Toolbar
        this.mToolbar.setNavigationIcon(R.mipmap.ic_launcher);
        this.mToolbar.setTitle(R.string.main_screen_title);
        this.setSupportActionBar(this.mToolbar);

        // Use Fragments
        if (savedInstanceState == null) {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new MainFragment(), MainFragment.class.getSimpleName())
                    .commit();
        }
    }
}

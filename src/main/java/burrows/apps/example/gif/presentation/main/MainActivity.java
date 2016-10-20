package burrows.apps.example.gif.presentation.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.presentation.SchedulerProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

import javax.inject.Inject;

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainActivity extends AppCompatActivity {
  @BindView(R.id.tool_bar) Toolbar toolbar;
  @Inject RiffsyRepository repository;
  @Inject SchedulerProvider schedulerProvider;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    // Injection dependencies
    ((App) getApplication()).getActivityComponent().inject(this);

    // Bind views
    ButterKnife.bind(this);

    // Setup Toolbar
    toolbar.setNavigationIcon(R.mipmap.ic_launcher);
    toolbar.setTitle(R.string.main_screen_title);
    setSupportActionBar(toolbar);

    // Use Fragments
    MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
    if (fragment == null) {
      fragment = new MainFragment();
    }

    if (savedInstanceState == null) {
      getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content_frame, fragment, MainFragment.class.getSimpleName())
        .commit();
    }

    // Create presenter
    new MainPresenter(fragment, repository, schedulerProvider);
  }
}

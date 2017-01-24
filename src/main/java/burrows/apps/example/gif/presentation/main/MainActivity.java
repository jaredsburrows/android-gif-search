package burrows.apps.example.gif.presentation.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.databinding.ActivityMainBinding;
import burrows.apps.example.gif.presentation.SchedulerProvider;

import javax.inject.Inject;

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainActivity extends AppCompatActivity {
  @Inject RiffsyRepository repository;
  @Inject SchedulerProvider schedulerProvider;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    // Injection dependencies
    ((App) getApplication()).activityComponent.inject(this);

    // Setup Toolbar
    binding.toolBar.setNavigationIcon(R.mipmap.ic_launcher);
    binding.toolBar.setTitle(R.string.main_screen_title);
    setSupportActionBar(binding.toolBar);

    // Use Fragments
    MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
    if (fragment == null) fragment = new MainFragment();
    if (savedInstanceState == null) getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.content_frame, fragment, MainFragment.class.getSimpleName())
      .commit();

    // Create presenter
    new MainPresenter(fragment, repository, schedulerProvider);
  }
}

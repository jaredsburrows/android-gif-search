package burrows.apps.giphy.example.di.component;

import android.app.Application;
import android.content.Context;
import burrows.apps.giphy.example.App;
import burrows.apps.giphy.example.di.module.AppModule;
import burrows.apps.giphy.example.di.module.LeakCanaryModule;
import burrows.apps.giphy.example.di.module.RxModule;
import burrows.apps.giphy.example.rx.RxBus;
import burrows.apps.giphy.example.ui.activity.MainActivity;
import burrows.apps.giphy.example.ui.fragment.MainFragment;
import com.squareup.leakcanary.RefWatcher;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Singleton
@Component(modules = {AppModule.class, RxModule.class, LeakCanaryModule.class})
public interface AppComponent {
  void inject(final App app);
  void inject(final MainActivity mainActivity);
  void inject(final MainFragment mainFragment);

  Application application();
  Context context();
  RxBus bus();
  RefWatcher refWatcher();
}

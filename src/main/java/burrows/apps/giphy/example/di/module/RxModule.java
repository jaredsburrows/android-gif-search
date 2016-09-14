package burrows.apps.giphy.example.di.module;

import burrows.apps.giphy.example.rx.RxBus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class RxModule {
  @Provides @Singleton RxBus provideRxBus() {
    return new RxBus();
  }
}

package burrows.apps.example.gif.di.module;

import burrows.apps.example.gif.rx.RxBus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public final class RxModule {
  @Provides @Singleton RxBus provideRxBus() {
    return new RxBus();
  }
}

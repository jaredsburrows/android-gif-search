package burrows.apps.example.gif.di.module;

import android.content.Context;
import burrows.apps.example.gif.di.scope.PerActivity;
import burrows.apps.example.gif.rest.service.ImageDownloader;
import dagger.Module;
import dagger.Provides;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class GlideModule {
  @Provides @PerActivity protected ImageDownloader provideImageDownloader(Context context) {
    return new ImageDownloader(context);
  }
}

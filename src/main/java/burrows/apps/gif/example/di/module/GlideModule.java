package burrows.apps.gif.example.di.module;

import android.content.Context;
import burrows.apps.gif.example.di.scope.PerActivity;
import burrows.apps.gif.example.rest.service.ImageDownloader;
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

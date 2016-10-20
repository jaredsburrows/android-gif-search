package burrows.apps.example.gif.presentation.di.module;

import android.content.Context;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.presentation.di.scope.PerActivity;
import dagger.Module;
import dagger.Provides;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class GlideModule {
  @Provides @PerActivity protected ImageRepository provideImageDownloader(Context context) {
    return new ImageRepository(context);
  }
}

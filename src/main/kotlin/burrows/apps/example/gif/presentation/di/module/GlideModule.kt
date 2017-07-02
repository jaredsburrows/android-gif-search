package burrows.apps.example.gif.presentation.di.module

import android.content.Context
import burrows.apps.example.gif.data.rest.repository.ImageApiRepository
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import dagger.Module
import dagger.Provides

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
open class GlideModule {
  @Provides @PerActivity open fun providesImageDownloader(context: Context): ImageApiRepository {
    return ImageApiRepository(context)
  }
}

package burrows.apps.example.gif.presentation.di.module

import android.content.Context
import burrows.apps.example.gif.data.rest.repository.ImageRepository
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import dagger.Module
import dagger.Provides

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
open class GlideModule {
  @Provides @PerActivity open fun provideImageDownloader(context: Context): ImageRepository {
    return ImageRepository(context)
  }
}

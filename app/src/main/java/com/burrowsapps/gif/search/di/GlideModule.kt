package com.burrowsapps.gif.search.di

import android.content.Context
import android.graphics.Bitmap.CompressFormat.WEBP_LOSSY
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.burrowsapps.gif.search.BuildConfig.DEBUG
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.di.ApplicationMode.TESTING
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors.fromApplication
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.InputStream

/** Custom Glide module. */
@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
internal class GlideModule : AppGlideModule() {
  @EntryPoint
  @InstallIn(SingletonComponent::class)
  internal interface GlideModuleEntryPoint {
    fun provideOkHttpClient(): OkHttpClient

    fun provideApplicationMode(): ApplicationMode
  }

  private fun getGlideEntryPoint(context: Context): GlideModuleEntryPoint =
    fromApplication(context.applicationContext, GlideModuleEntryPoint::class.java)

  override fun applyOptions(
    context: Context,
    builder: GlideBuilder,
  ) {
    val applicationMode = getGlideEntryPoint(context).provideApplicationMode()

    builder
      .setDefaultRequestOptions(
        RequestOptions()
          .encodeFormat(WEBP_LOSSY)
          .encodeQuality(ENCODE_QUALITY)
          .diskCacheStrategy(ALL)
          .error(R.mipmap.ic_launcher)
          .fallback(R.mipmap.ic_launcher),
      ).setLogLevel(if (applicationMode == TESTING || DEBUG) Log.WARN else Log.ERROR)
      .setIsActiveResourceRetentionAllowed(true)
  }

  override fun registerComponents(
    context: Context,
    glide: Glide,
    registry: Registry,
  ) {
    // Reuse the app's OkHttpClient (so image fetches share its Dispatcher + ConnectionPool for
    // connection reuse) but strip the HTTP disk cache. Glide already keeps its own source/result
    // disk cache, so letting OkHttp cache image bytes too would (1) store every GIF on disk twice
    // and (2) let large image responses evict the small Klipy API JSON that the shared 50 MiB cache
    // exists to retain. newBuilder() shares the pools; cache(null) drops only the duplicate cache.
    val imageClient =
      getGlideEntryPoint(context)
        .provideOkHttpClient()
        .newBuilder()
        .cache(null)
        .build()

    registry.replace(
      GlideUrl::class.java,
      InputStream::class.java,
      OkHttpUrlLoader.Factory(imageClient),
    )
  }

  private companion object {
    private const val ENCODE_QUALITY = 85
  }
}

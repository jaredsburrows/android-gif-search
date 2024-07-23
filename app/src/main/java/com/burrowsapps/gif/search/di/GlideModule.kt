package com.burrowsapps.gif.search.di

import android.content.Context
import android.graphics.Bitmap.CompressFormat.WEBP_LOSSLESS
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
    fun getOkHttpClient(): OkHttpClient

    fun getApplicationMode(): ApplicationMode
  }

  private fun getGlideEntryPoint(context: Context): GlideModuleEntryPoint {
    return fromApplication(context.applicationContext, GlideModuleEntryPoint::class.java)
  }

  override fun applyOptions(
    context: Context,
    builder: GlideBuilder,
  ) {
    val applicationMode = getGlideEntryPoint(context).getApplicationMode()

    builder.setDefaultRequestOptions(
      RequestOptions()
        .encodeFormat(WEBP_LOSSLESS)
        .encodeQuality(ENCODE_QUALITY)
        .diskCacheStrategy(ALL)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher),
    )
      .setLogLevel(if (applicationMode == TESTING || DEBUG) Log.WARN else Log.ERROR)
      .setIsActiveResourceRetentionAllowed(true)
  }

  override fun registerComponents(
    context: Context,
    glide: Glide,
    registry: Registry,
  ) {
    val okHttpClient = getGlideEntryPoint(context).getOkHttpClient()

    registry.replace(
      GlideUrl::class.java,
      InputStream::class.java,
      OkHttpUrlLoader.Factory(okHttpClient),
    )
  }

  private companion object {
    private const val ENCODE_QUALITY = 100
  }
}

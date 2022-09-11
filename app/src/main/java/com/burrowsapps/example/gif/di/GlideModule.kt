package com.burrowsapps.example.gif.di

import android.content.Context
import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.Bitmap.CompressFormat.WEBP_LOSSLESS
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.burrowsapps.example.gif.BuildConfig.DEBUG
import com.burrowsapps.example.gif.R
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors.fromApplication
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.InputStream

@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
internal class GlideModule : AppGlideModule() {
  @EntryPoint
  @InstallIn(SingletonComponent::class)
  internal interface GlideModuleEntryPoint {
    fun defaultOkHttpClient(): OkHttpClient
  }

  override fun applyOptions(context: Context, builder: GlideBuilder) {
    builder.setDefaultRequestOptions(
      RequestOptions()
        .encodeFormat(if (VERSION.SDK_INT >= VERSION_CODES.R) WEBP_LOSSLESS else PNG)
        .encodeQuality(ENCODE_QUALITY)
        .diskCacheStrategy(ALL)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
    ).setDiskCache(
      InternalCacheDiskCacheFactory(
        context.applicationContext, GLIDE_CACHE_DIRECTORY, DEFAULT_DISK_CACHE_SIZE.toLong()
      )
    )
      .setBitmapPool(LruBitmapPool(DEFAULT_DISK_CACHE_SIZE.toLong()))
      .setMemoryCache(LruResourceCache(DEFAULT_DISK_CACHE_SIZE.toLong()))
      .setLogRequestOrigins(DEBUG)
      .setLogLevel(if (DEBUG) Log.DEBUG else Log.INFO)
  }

  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    val okHttpClient = fromApplication(
      context.applicationContext,
      GlideModuleEntryPoint::class.java
    ).defaultOkHttpClient()

    registry.replace(
      GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient)
    )
  }

  private companion object {
    private const val GLIDE_CACHE_DIRECTORY = "https-image-cache"
    private const val ENCODE_QUALITY = 100
  }
}

package com.burrowsapps.example.gif.di

import android.content.Context
import android.graphics.Bitmap.CompressFormat.PNG
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888
import com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
import com.bumptech.glide.load.engine.cache.DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.request.RequestOptions
import com.burrowsapps.example.gif.BuildConfig.DEBUG
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.di.NetworkModule.CLIENT_CACHE_SIZE

@GlideModule
class AppGlideModule : com.bumptech.glide.module.AppGlideModule() {
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    builder.setDefaultRequestOptions(
      RequestOptions()
        .encodeFormat(PNG)
        .encodeQuality(ENCODE_QUALITY)
        .diskCacheStrategy(ALL)
        .format(PREFER_ARGB_8888)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
    ).setDiskCache(
      InternalCacheDiskCacheFactory(
        context, GLIDE_CACHE_DIRECTORY, DEFAULT_DISK_CACHE_SIZE.toLong()
      )
    ).setMemoryCache(LruResourceCache(CLIENT_CACHE_SIZE))
      .setLogRequestOrigins(DEBUG)
  }

  companion object {
    private const val GLIDE_CACHE_DIRECTORY = "https-image-cache"
    private const val ENCODE_QUALITY = 100
  }
}
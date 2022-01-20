package com.burrowsapps.example.gif.di

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.burrowsapps.example.gif.di.NetworkModule.CLIENT_CACHE_SIZE

@GlideModule
class AppGlideModule : com.bumptech.glide.module.AppGlideModule() {
  override fun applyOptions(context: Context, builder: GlideBuilder) {
//    builder.setDefaultRequestOptions()
    builder.setDiskCache(
      InternalCacheDiskCacheFactory(
        context, GLIDE_CACHE_DIRECTORY, DEFAULT_DISK_CACHE_SIZE.toLong()
      )
    )
    builder.setMemoryCache(LruResourceCache(CLIENT_CACHE_SIZE))
  }

  companion object {
    private const val GLIDE_CACHE_DIRECTORY = "https-image-cache"
  }
}

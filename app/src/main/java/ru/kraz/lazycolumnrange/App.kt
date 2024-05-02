package ru.kraz.lazycolumnrange

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.util.DebugLogger

class App : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader =
        ImageLoader(this).newBuilder()
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.02)
                    .directory(cacheDir)
                    .build()
            }
            .logger(DebugLogger())
            .build()
}
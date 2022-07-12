package com.gibsonruitiari.asobi.di

import android.app.ActivityManager
import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class AsobiGlideModule:AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val discCacheSizeBytes =  1024 * 1024 * 500 // 500 MB
        builder.setDiskCache(InternalCacheDiskCacheFactory(context,discCacheSizeBytes.toLong()))
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        if (activityManager?.isLowRamDevice ==true){
            builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
        }
    }
}
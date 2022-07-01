package com.gibsonruitiari.asobi.data.network.networkextensions

import okhttp3.*
import java.util.concurrent.TimeUnit


private val DEFAULT_CACHE_CONTROL = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()
private val DEFAULT_HEADERS = Headers.Builder().build()
internal fun get(url:String, headers:Headers= DEFAULT_HEADERS,
                 cacheControl: CacheControl= DEFAULT_CACHE_CONTROL
): Request {
    return Request.Builder()
        .url(url)
        .headers(headers)
        .cacheControl(cacheControl)
        .build()
}


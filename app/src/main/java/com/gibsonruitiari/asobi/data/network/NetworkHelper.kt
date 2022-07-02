package com.gibsonruitiari.asobi.data.network

import com.gibsonruitiari.asobi.data.network.interceptors.UserAgentInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal object NetworkHelper {
    private val baseClientBuilder:OkHttpClient.Builder
    get() {
        return OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .addInterceptor(UserAgentInterceptor())
            .dohCloudFlare()
            .connectionPool(ConnectionPool(10,2,TimeUnit.MINUTES))
    }
    val client by lazy { baseClientBuilder.build() }
}
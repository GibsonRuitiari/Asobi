package com.gibsonruitiari.asobi.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

internal class UserAgentInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return if (originalRequest.header("user-agent").isNullOrEmpty()){
            val newRequest = originalRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent","")
                .build()
            chain.proceed(newRequest)
        }else chain.proceed(originalRequest)
    }
}
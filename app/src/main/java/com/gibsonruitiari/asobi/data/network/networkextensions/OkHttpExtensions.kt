package com.gibsonruitiari.asobi.data.network.networkextensions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import kotlin.coroutines.resumeWithException


@OptIn(ExperimentalCoroutinesApi::class)
internal suspend fun Call.awaitBody():Response = suspendCancellableCoroutine {
    enqueue(object:Callback{
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful){
                it.resumeWithException(Exception(response.message))
            }else it.resume(response){
                response.body?.closeQuietly()
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            it.resumeWithException(e)
        }
    })
}
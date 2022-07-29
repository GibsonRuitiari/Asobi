package com.gibsonruitiari.asobi.utilities

import com.gibsonruitiari.asobi.data.network.NetworkResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.io.IOException

fun<T> Flow<T>.toNetworkResource(): Flow<NetworkResource<T>> = flow{
    onStart { emit(NetworkResource.loading(null)) }
    retryWhen { cause, attempt ->
        when {
            cause is IOException && attempt <3 -> {
                delay(400)
                return@retryWhen  true
            }
            else -> return@retryWhen false
        }
    }
    catch { emit(NetworkResource.error(it,null)) }
    collect{emit(NetworkResource.success(it))}
}
package com.gibsonruitiari.asobi.utilities.utils

import com.gibsonruitiari.asobi.data.network.NetworkResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

fun<T> Flow<T>.toNetworkResource(): Flow<NetworkResource<T>> = flow{
    onStart { emit(NetworkResource.loading(null)) }
    catch { emit(NetworkResource.error(it,null)) }
    collect{emit(NetworkResource.success(it))}
}
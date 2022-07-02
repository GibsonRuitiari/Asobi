package com.gibsonruitiari.asobi.data.network

import com.gibsonruitiari.asobi.common.logging.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.net.HttpRetryException
import java.net.UnknownHostException

sealed class NetworkState{
    object EMPTY:NetworkState()
    object LOADING:NetworkState()
    object SUCCESS:NetworkState()
    data class Error(val errorMessage:String?=null):NetworkState()
}
sealed class NetworkResource <out T>{
    data class Success<out T>(val data:T):NetworkResource<T>()
    object Loading:NetworkResource<Nothing>()
    data class Error(val errorMessage:String?, val throwable: Throwable):NetworkResource<Nothing>()
}

/**
 * By default work on the IO Dispatcher when making an apiCall
 */
suspend fun<T> asynchronousApiCall(apiCall:suspend ()->T,
                                   logger: Logger,
                                   apiCallDispatcher:CoroutineDispatcher=Dispatchers.IO):NetworkResource<T>{
    return withContext(apiCallDispatcher){
        try {
            NetworkResource.Success(apiCall.invoke())
        }catch (throwable:Throwable){
            logger.e("The following error $throwable occurred when invoking the following api call $apiCall")
            when(throwable){
                // io exception encapsulates all http errors eg UnknownHostException etc
                is IOException-> NetworkResource.Error(throwable = throwable, errorMessage = throwable.message)
                else-> NetworkResource.Error(null,throwable)
            }
        }
    }
}

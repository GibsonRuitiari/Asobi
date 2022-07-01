package com.gibsonruitiari.asobi.data.network

sealed class NetworkResource <out T>{
    data class Success<out T>(val data:T):NetworkResource<T>()
    object Loading:NetworkResource<Nothing>()
    data class Error(val errorMessage:String?, val throwable: Throwable):NetworkResource<Nothing>()
}
package com.gibsonruitiari.asobi.data.network


data class NetworkResource <out T>(val status: Status,
val data: T?, val throwable: Throwable?){
    companion object{
        fun <T> success(data: T?): NetworkResource<T> {
            return NetworkResource(Status.SUCCESS, data, null)
        }

        fun <T> error(throwable: Throwable, data: T?): NetworkResource<T> {
            return NetworkResource(Status.ERROR, data, throwable)
        }

        fun <T> loading(data: T?): NetworkResource<T> {
            return NetworkResource(Status.LOADING, data, null)
        }
    }

}
enum class Status {
    SUCCESS, ERROR, LOADING,
}

/**
 * By default work on the IO Dispatcher when making an apiCall
 */
//suspend fun<T> asynchronousApiCall(apiCall:suspend ()->T,
//                                   logger: Logger,
//                                   apiCallDispatcher:CoroutineDispatcher=Dispatchers.IO):NetworkResource<T>{
//    return withContext(apiCallDispatcher){
//        try {
//            NetworkResource.Success(apiCall.invoke())
//        }catch (throwable:Throwable){
//            logger.e("The following error $throwable occurred when invoking the following api call $apiCall")
//            when(throwable){
//                // io exception encapsulates all http errors eg UnknownHostException etc
//                is IOException-> NetworkResource.Error(throwable = throwable, errorMessage = throwable.message)
//                else-> NetworkResource.Error(null,throwable)
//            }
//        }
//    }
//}

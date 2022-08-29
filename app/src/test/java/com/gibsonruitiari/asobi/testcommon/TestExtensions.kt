package com.gibsonruitiari.asobi.testcommon

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
inline fun TestScope.performTest(crossinline action:suspend ()->Unit){
    val scopeJob =launch { action.invoke() }
    advanceUntilIdle()
    scopeJob.cancel()
}


suspend fun<T> Flow<T>.getOrThrow(errorMessage:String?=null):T= firstOrNull() ?:
throw IllegalStateException(errorMessage)
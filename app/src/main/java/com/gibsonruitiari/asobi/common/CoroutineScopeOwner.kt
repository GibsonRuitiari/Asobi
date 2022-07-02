package com.gibsonruitiari.asobi.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

interface CoroutineScopeOwner {
    val coroutineScope:CoroutineScope
    fun workerDispatcher():CoroutineDispatcher = Dispatchers.IO

}
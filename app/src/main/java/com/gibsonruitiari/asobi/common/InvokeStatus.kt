package com.gibsonruitiari.asobi.common

sealed interface InvokeStatus
object InvokeStarted:InvokeStatus
object InvokeSuccess:InvokeStatus
data class InvokeError(val throwable: Throwable):InvokeStatus

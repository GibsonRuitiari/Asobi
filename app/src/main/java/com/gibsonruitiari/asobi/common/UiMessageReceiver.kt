package com.gibsonruitiari.asobi.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

// propagates messages to the ui, each message is differentiated by its id
data class UiMessageReceiver(val message:String,
val id:Long=UUID.randomUUID().mostSignificantBits)

fun UiMessageReceiver(throwable: Throwable,
id:Long=UUID.randomUUID().mostSignificantBits):UiMessageReceiver=UiMessageReceiver(
    message = throwable.message ?: "Error occurred: $throwable",
id=id)
class UiMessageManager{
    private val mutex= Mutex()
    private val _messages = MutableStateFlow(emptyList<UiMessageReceiver>())
    // emit the current/latest message and display it to the uo
    val message:Flow<UiMessageReceiver?> = _messages.map {
     it.firstOrNull()
    }.distinctUntilChanged()
    suspend fun emitMessage(message:UiMessageReceiver){
        mutex.withLock {
            _messages.value = _messages.value+message // add the message to the queue
        }
    }
    suspend fun clearMessage(id:Long){
        mutex.withLock {
            _messages.value = _messages.value.filterNot { it.id==id}
        }
    }

}
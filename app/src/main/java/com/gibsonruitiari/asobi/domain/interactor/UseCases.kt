package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibsonruitiari.asobi.common.InvokeError
import com.gibsonruitiari.asobi.common.InvokeStarted
import com.gibsonruitiari.asobi.common.InvokeStatus
import com.gibsonruitiari.asobi.common.InvokeSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

abstract class Interactor<in Params>{
    operator fun invoke(params: Params, timeoutInMs:Long=TimeUnit.MINUTES.toMillis(5)):Flow<InvokeStatus> = flow{
        try {
            withTimeout(timeoutInMs){
                emit(InvokeStarted)
                doWork(params)
                emit(InvokeSuccess)
            }
        }catch (t:TimeoutCancellationException){
            emit(InvokeError(t))
        }
    }.catch { t->emit(InvokeError(t)) }
    protected abstract suspend fun doWork(params:Params)
    suspend fun executeSynchronously(params: Params) = doWork(params)
}
abstract class PaginatedEntriesUseCase<Input :PaginatedEntriesUseCase.PaginatedParams<Output>,Output:Any>:SubjectInteractor<Input,
        PagingData<Output>>() {
    interface PaginatedParams<Output:Any>{
        val pagingConfig:PagingConfig
    }
}
abstract class SubjectInteractor<Input:Any,Output>{
    private val paramsState = MutableSharedFlow<Input>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST,
    extraBufferCapacity = 1)
    val flowObservable:Flow<Output> =
        paramsState.distinctUntilChanged()
            .flatMapLatest { createObservable(it) }
            .distinctUntilChanged()
    operator fun invoke(params:Input){
        paramsState.tryEmit(params)
    }
    protected abstract fun createObservable(params:Input):Flow<Output>
}
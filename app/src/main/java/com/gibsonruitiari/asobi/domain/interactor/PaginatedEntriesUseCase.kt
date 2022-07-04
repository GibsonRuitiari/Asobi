package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

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
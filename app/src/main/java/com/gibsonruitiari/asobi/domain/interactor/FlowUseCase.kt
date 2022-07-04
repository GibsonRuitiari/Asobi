package com.gibsonruitiari.asobi.domain.interactor

import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

abstract class FlowUseCase<in Input, Output> {
    var coroutineJob: Job? = null
    abstract fun run(params: Input): Flow<Output>
    operator fun invoke(params: Input): Flow<Output> = run(params)


}
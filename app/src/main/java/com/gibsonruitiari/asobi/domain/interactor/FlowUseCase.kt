package com.gibsonruitiari.asobi.domain.interactor

import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

abstract class FlowUseCase<in Input, Output> {
    var coroutineJob: Job? = null
    abstract fun run(params: Input): Flow<Output>
    operator fun invoke(params: Input): Flow<Output> = run(params)

    /**
     * Asynchronously execute the use case,
     * consume the input from flow on UI thread. Any running job is cancelled by default
     * upon calling this method repeatedly (can be changed in config)
     * One the execution finishes, onComplete() is called
     */
    fun CoroutineScopeOwner.execute(params: Input,
    config: FlowUseCaseConfig.Builder<Output>.()->Unit){
        val flowUseCaseConfig= FlowUseCaseConfig.Builder<Output>()
            .run {
                config(this)
                return@run build()
            }
        if (flowUseCaseConfig.disposePrevious) coroutineJob?.cancel()
        coroutineJob = run(params)
            .flowOn(workerDispatcher())
            .onStart { flowUseCaseConfig.onStart() }
            .onEach { flowUseCaseConfig.onNext(it) }
            .onCompletion {
                error->
                when{
                    error is CancellationException->{
                        // ignore this
                    }
                    error!=null-> flowUseCaseConfig.onError(error)
                    // there is no error so call on complete
                    else->flowUseCaseConfig.onComplete()
                }
            }.catch { // not needed
                 }
            .launchIn(coroutineScope)
    }
    /**
     * Holds references to lambdas and some basic configuration
     * used to process results of Flow use case.
     * Use [FlowUseCaseConfig.Builder] to construct this object.
     */
    class FlowUseCaseConfig<T> private constructor(
        val onStart: () -> Unit,
        val onNext: (T) -> Unit,
        val onError: (Throwable) -> Unit,
        val onComplete: () -> Unit,
        val disposePrevious: Boolean
    ) {
        class Builder<T> {
            private var onStart: (() -> Unit)? = null
            private var onNext: ((T) -> Unit)? = null
            private var onError: ((Throwable) -> Unit)? = null
            private var onComplete: (() -> Unit)? = null
            private var disposePrevious = true

            /**
             * Set lambda that is called right before
             * internal Job of Flow is launched.
             * @param onStart Lambda called right before Flow Job is launched.
             */
            fun onStart(onStart: () -> Unit) {
                this.onStart = onStart
            }

            /**
             * Set lambda that is called when internal Flow emits new value
             * @param onNext Lambda called for every new emitted value
             */
            fun onNext(onNext: (T) -> Unit) {
                this.onNext = onNext
            }

            /**
             * Set lambda that is called when some exception on
             * internal Flow occurs
             * @param onError Lambda called when exception occurs
             */
            fun onError(onError: (Throwable) -> Unit) {
                this.onError = onError
            }

            /**
             * Set lambda that is called when internal Flow is completed
             * without errors
             * @param onComplete Lambda called when Flow is completed
             * without errors
             */
            fun onComplete(onComplete: () -> Unit) {
                this.onComplete = onComplete
            }

            /**
             * Set whether currently running Job of internal Flow
             * should be canceled when execute is called repeatedly.
             * @param disposePrevious True if currently running
             * Job of internal Flow should be canceled
             */
            fun disposePrevious(disposePrevious: Boolean) {
                this.disposePrevious = disposePrevious
            }

            internal fun build(): FlowUseCaseConfig<T> {
                return FlowUseCaseConfig(onStart ?: {}, onNext ?: {},
                    onError ?: { throw it }, onComplete ?: {},
                    disposePrevious
                )
            }
        }
    }
}
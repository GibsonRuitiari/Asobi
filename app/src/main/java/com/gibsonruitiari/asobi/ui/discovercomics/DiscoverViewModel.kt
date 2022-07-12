package com.gibsonruitiari.asobi.ui.discovercomics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.domain.CoroutineScopeOwner
import com.gibsonruitiari.asobi.utilities.Store
import com.gibsonruitiari.asobi.domain.DiscoverComicsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscoverViewModel constructor(private val discoverComicsUseCase: DiscoverComicsUseCase
):ViewModel(), CoroutineScopeOwner,Store<DiscoverComicsState,
        DiscoverComicsAction, DiscoverComicsSideEffect>{
    override val coroutineScope: CoroutineScope
        get() = viewModelScope
    private val state = MutableStateFlow(DiscoverComicsState.Empty)
    // broadcast all the side effects to all subscribers
    // replay=0 so as to broadcast most recent side effect
    private val sideEffect = MutableSharedFlow<DiscoverComicsSideEffect>()
    init {
        onAction(DiscoverComicsAction.LoadComics)
    }

    override fun observeState(): StateFlow<DiscoverComicsState> = state
    override fun observeSideEffect(): Flow<DiscoverComicsSideEffect> = sideEffect
    companion object{
        val PARAMS= DiscoverComicsUseCase.DiscoverComicsParams(10,1)
    }
    override fun onAction(action: DiscoverComicsAction) {
        val oldState = state.value
        when(action){
            is DiscoverComicsAction.LoadComics->{

                with(state){
                    discoverComicsUseCase.execute(args = PARAMS){
                        onStart {
                            coroutineScope.launch {
                                emit(oldState.copy(isLoading = false))
                            }
                        }
                        onNext {
                            coroutineScope.launch {
                                emit(oldState.copy(isLoading = false,
                                    comicsData = it
                                ))
                            }
                        }
                        onError {
                            coroutineScope.launch { emit(oldState.copy(isLoading = false)) }
                            onAction(DiscoverComicsAction.Error(it.message?:"Something went wrong please try again"))
                        }
                    }
                }
            }
            is DiscoverComicsAction.Error->{
                coroutineScope.launch {
                    sideEffect.emit(DiscoverComicsSideEffect.Error(action.message))
                }
            }
        }
    }

}
package com.gibsonruitiari.asobi.ui.discovercomics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.domain.CoroutineScopeOwner
import com.gibsonruitiari.asobi.utilities.Store
import com.gibsonruitiari.asobi.domain.DiscoverComicsUseCase
import com.gibsonruitiari.asobi.utilities.extensions.RetryTrigger
import com.gibsonruitiari.asobi.utilities.extensions.retryFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiscoverViewModel constructor(private val discoverComicsUseCase: DiscoverComicsUseCase):ViewModel(), CoroutineScopeOwner,Store<DiscoverComicsState,
        DiscoverComicsAction, DiscoverComicsSideEffect>{
    override val coroutineScope: CoroutineScope
        get() = viewModelScope
    private val state = MutableStateFlow(DiscoverComicsState.Empty)
    // broadcast all the side effects to all subscribers
    // replay=0 so as to broadcast most recent side effect
    private val sideEffect = MutableSharedFlow<DiscoverComicsSideEffect>()
    private val retryTrigger = RetryTrigger()
    init {
        onAction(DiscoverComicsAction.LoadComics)
    }
    fun retry(){
        retryTrigger.retry()
    }
    override fun observeState(): StateFlow<DiscoverComicsState> = retryFlow(retryTrigger = retryTrigger, source = {state})
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(500), initialValue = DiscoverComicsState.Empty)

    override fun observeSideEffect(): Flow<DiscoverComicsSideEffect> = sideEffect
    companion object{
        private const val ITEM_SIZE=16
        private const val ITEM_PAGE=1
    }

    override fun onAction(action: DiscoverComicsAction) {
        val oldState = state.value
        val params= DiscoverComicsUseCase.DiscoverComicsParams(ITEM_SIZE, ITEM_PAGE, genre = Genres.DC_COMICS)
        when(action){
            is DiscoverComicsAction.LoadComics->{
                with(state){
                    discoverComicsUseCase.execute(args = params){
                        onStart {
                            coroutineScope.launch {
                                emit(oldState.copy(isLoading = true))
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
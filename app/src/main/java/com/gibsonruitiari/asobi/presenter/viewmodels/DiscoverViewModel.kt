package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.interactor.observers.ObserveLatestComics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class DiscoverViewModel constructor(val latestComicsObserver:ObserveLatestComics
):ViewModel(){
    init {
        latestComicsObserver(ObserveLatestComics.Params(10))
    }
    private val refreshFlow = MutableStateFlow(0)
    @OptIn(ExperimentalCoroutinesApi::class)
    val discoverState:StateFlow<List<SManga>> = refreshFlow.flatMapLatest {
        latestComicsObserver.flowObservable
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )
    fun refresh(){
       refreshFlow.value +=1
    }

    override fun onCleared() {
        super.onCleared()
        refreshFlow.value=0
    }
}
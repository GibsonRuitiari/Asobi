package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import com.gibsonruitiari.asobi.common.Store
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsAction
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsSideEffect
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class DiscoverViewModel:ViewModel(),Store<DiscoverComicsState,
        DiscoverComicsAction,DiscoverComicsSideEffect>,CoroutineScopeOwner {
    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override fun observeSideEffect(): Flow<DiscoverComicsSideEffect> {
        TODO("Not yet implemented")
    }

    override fun observeState(): StateFlow<DiscoverComicsState> {
        TODO("Not yet implemented")
    }

    override fun onAction(action: DiscoverComicsAction) {
        TODO("Not yet implemented")
    }
}
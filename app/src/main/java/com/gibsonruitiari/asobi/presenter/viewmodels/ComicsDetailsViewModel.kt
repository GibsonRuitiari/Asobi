package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import com.gibsonruitiari.asobi.common.Store
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicDetailsAction
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicDetailsSideEffect
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicDetailsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ComicsDetailsViewModel:ViewModel(),
CoroutineScopeOwner,Store<ComicDetailsState,
ComicDetailsAction,ComicDetailsSideEffect>{
    override val coroutineScope: CoroutineScope
        get() = TODO("Not yet implemented")

    override fun observeState(): StateFlow<ComicDetailsState> {
        TODO("Not yet implemented")
    }

    override fun observeSideEffect(): Flow<ComicDetailsSideEffect> {
        TODO("Not yet implemented")
    }

    override fun onAction(action: ComicDetailsAction) {
        TODO("Not yet implemented")
    }

}
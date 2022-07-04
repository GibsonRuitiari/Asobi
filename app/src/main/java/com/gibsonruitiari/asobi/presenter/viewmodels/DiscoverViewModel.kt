package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import com.gibsonruitiari.asobi.common.Store
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.domain.interactor.PagedCompletedComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedLatestComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedOngoingComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedPopularComicsObserver
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsAction
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsSideEffect
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class DiscoverViewModel constructor(private val logger: Logger,
 val pagedCompletedComicsObserver:PagedCompletedComicsObserver,
 val pagedOngoingComicsObserver: PagedOngoingComicsObserver,
 val pagedLatestComicsObserver: PagedLatestComicsObserver,
 val pagedPopularComicsObserver: PagedPopularComicsObserver):ViewModel(){
   //  private val latestLoadingState
}
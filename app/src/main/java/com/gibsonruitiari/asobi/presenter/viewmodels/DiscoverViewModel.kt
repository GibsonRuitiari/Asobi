package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.map
import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import com.gibsonruitiari.asobi.common.Store
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.common.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.domain.interactor.PagedCompletedComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedLatestComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedOngoingComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedPopularComicsObserver
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsAction
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsSideEffect
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsState
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverScreenViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class DiscoverViewModel constructor(private val logger: Logger,
 val pagedCompletedComicsObserver:PagedCompletedComicsObserver,
 val pagedOngoingComicsObserver: PagedOngoingComicsObserver,
 val pagedLatestComicsObserver: PagedLatestComicsObserver,
 val pagedPopularComicsObserver: PagedPopularComicsObserver):ViewModel(){
   //  private val latestLoadingState
  val state:StateFlow<DiscoverScreenViewState> = combine(pagedCompletedComicsObserver.flowObservable,
   pagedOngoingComicsObserver.flowObservable,
   pagedLatestComicsObserver.flowObservable,
   pagedPopularComicsObserver.flowObservable){
   completed,ongoing,latest,popular->
    val completedComics=completed.map { sMangaToViewComicMapper(it)}
    val ongoingComics=ongoing.map { sMangaToViewComicMapper(it) }
    val latestComics=latest.map { sMangaToViewComicMapper(it) }
    val popularComics=popular.map { sMangaToViewComicMapper(it) }
     DiscoverScreenViewState()
   }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000),
   initialValue = DiscoverScreenViewState.emptyState)
 init {
     pagedPopularComicsObserver(PagedPopularComicsObserver.PagedPopularComicsParams(PagingConfig(10)))
     pagedLatestComicsObserver(PagedLatestComicsObserver.LatestComicsParams(PagingConfig(10)))
     pagedOngoingComicsObserver(PagedOngoingComicsObserver.OngoingComicsParams(PagingConfig(10)))
     pagedCompletedComicsObserver(PagedCompletedComicsObserver.CompletedComicsParams(PagingConfig(10)))
 }

}
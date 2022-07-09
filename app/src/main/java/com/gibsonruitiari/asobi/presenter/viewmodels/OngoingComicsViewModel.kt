package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.domain.interactor.pagedobservers.PagedOngoingComicsObserver
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow

class OngoingComicsViewModel  constructor(pagedOngoingComicsObserver: PagedOngoingComicsObserver):ViewModel(){
    val pagedList: Flow<PagingData<ViewComics>> = pagedOngoingComicsObserver
        .flowObservable
        .cachedIn(viewModelScope)
    init {
        pagedOngoingComicsObserver(PagedOngoingComicsObserver.OngoingComicsParams(pagingConfig))
    }
    companion object{
        val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
            enablePlaceholders = false)
    }
}
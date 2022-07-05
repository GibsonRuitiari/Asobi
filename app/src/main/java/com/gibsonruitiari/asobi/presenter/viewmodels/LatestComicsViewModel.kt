package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.domain.interactor.pagedobservers.PagedLatestComicsObserver
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow

class LatestComicsViewModel constructor(pagedLatestComicsObserver: PagedLatestComicsObserver):ViewModel(){
    init {
        pagedLatestComicsObserver(PagedLatestComicsObserver.LatestComicsParams(pagingConfig))
    }
    val pagedList:Flow<PagingData<ViewComics>> = pagedLatestComicsObserver
        .flowObservable
        .cachedIn(viewModelScope)
    companion object{
        val pagingConfig = PagingConfig(pageSize = 36,
            initialLoadSize = 36)
    }
}
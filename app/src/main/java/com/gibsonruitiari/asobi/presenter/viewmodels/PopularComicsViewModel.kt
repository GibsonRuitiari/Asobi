package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.domain.interactor.pagedobservers.PagedPopularComicsObserver
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow

class PopularComicsViewModel constructor(pagedPopularComicsObserver: PagedPopularComicsObserver):ViewModel() {
    val pagedList:Flow<PagingData<ViewComics>> = pagedPopularComicsObserver
        .flowObservable
        .cachedIn(viewModelScope)
    init {
        pagedPopularComicsObserver(PagedPopularComicsObserver.PagedPopularComicsParams(pagingConfig))
    }
    companion object{
        val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
        enablePlaceholders = false)
    }
}
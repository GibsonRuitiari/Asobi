package com.gibsonruitiari.asobi.ui.latestcomics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.domain.latestcomics.PagedLatestComicsObserver
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow

class LatestComicsViewModel constructor(pagedLatestComicsObserver: PagedLatestComicsObserver):ViewModel(){
    init {
        pagedLatestComicsObserver(PagedLatestComicsObserver.LatestComicsParams(pagingConfig))
    }
    val pagedList:Flow<PagingData<ViewComics>> = pagedLatestComicsObserver
        .flowObservable
        .cachedIn(viewModelScope)
    companion object{
        val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
            enablePlaceholders = true)
    }
}
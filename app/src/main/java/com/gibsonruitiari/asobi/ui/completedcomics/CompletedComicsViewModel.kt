package com.gibsonruitiari.asobi.ui.completedcomics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.domain.completedcomics.PagedCompletedComicsObserver
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow

class CompletedComicsViewModel constructor(pagedCompletedComicsObserver: PagedCompletedComicsObserver):ViewModel() {
    companion object{
        val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
            enablePlaceholders = false)
    }
    val pagedList: Flow<PagingData<ViewComics>> = pagedCompletedComicsObserver
        .flowObservable
        .cachedIn(viewModelScope)
    init {
        pagedCompletedComicsObserver(PagedCompletedComicsObserver.CompletedComicsParams(pagingConfig))
    }
}
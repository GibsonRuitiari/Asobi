package com.gibsonruitiari.asobi.ui.comicsbygenre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.domain.bygenre.PagedComicsByGenreObserver
import com.gibsonruitiari.asobi.ui.comicfilter.ComicFilterViewModel
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow

class ComicsByGenreViewModel constructor(pagedComicsByGenreObserver: PagedComicsByGenreObserver,filterViewModel: ComicFilterViewModel):ViewModel() {
    val comicsList: Flow<PagingData<ViewComics>> = pagedComicsByGenreObserver
        .flowObservable
        .cachedIn(viewModelScope)
    init {
        pagedComicsByGenreObserver(PagedComicsByGenreObserver.PagedComicsByGenreParams(genre =filterViewModel.selectedFilterChip.value.genres, pagingConfig = pagingConfig))
    }
    companion object{
        val pagingConfig = PagingConfig(pageSize = 36,
            initialLoadSize = 36)
    }
}
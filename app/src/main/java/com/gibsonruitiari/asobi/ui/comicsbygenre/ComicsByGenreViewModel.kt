package com.gibsonruitiari.asobi.ui.comicsbygenre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.domain.bygenre.PagedComicsByGenreObserver
import com.gibsonruitiari.asobi.ui.comicfilter.ComicFilterViewModel
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ComicsByGenreViewModel constructor(private val pagedComicsByGenreObserver: PagedComicsByGenreObserver):ViewModel(){
    val comicsList: Flow<PagingData<ViewComics>> = pagedComicsByGenreObserver
        .flowObservable
        .cachedIn(viewModelScope)
    private val genre_ = MutableStateFlow(Genres.DC_COMICS)
    fun setGenre(genre: Genres){
        genre_.value=genre
    }
    init {
        viewModelScope.launch {
           genre_.collectLatest {
               pagedComicsByGenreObserver(PagedComicsByGenreObserver.PagedComicsByGenreParams(it, pagingConfig))
           }
        }

    }

    companion object{
        val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
            enablePlaceholders = false)
    }
}
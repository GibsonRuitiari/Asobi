package com.gibsonruitiari.asobi.ui.comicsbygenre

import androidx.lifecycle.ViewModel
import androidx.paging.PagingConfig
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.domain.bygenre.PagedComicsByGenreObserver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class ComicsByGenreViewModel constructor(pagedComicsByGenreObserver: PagedComicsByGenreObserver):ViewModel() {
    val _genres = MutableStateFlow(Genres.DC_COMICS)
    val allGenres:SharedFlow<List<Genres>> = MutableSharedFlow<List<Genres>>().apply {
        tryEmit(Genres.values().toList())
    }
    fun setGenre(value:Genres){
        _genres.value = value
    }
    init {
        pagedComicsByGenreObserver(PagedComicsByGenreObserver.PagedComicsByGenreParams(genre = _genres.value, pagingConfig = pagingConfig))
    }
    companion object{
        val pagingConfig = PagingConfig(pageSize = 36,
            initialLoadSize = 36)
    }
}
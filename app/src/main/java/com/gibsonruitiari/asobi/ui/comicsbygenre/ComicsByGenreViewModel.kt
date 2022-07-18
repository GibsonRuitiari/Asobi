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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ComicsByGenreViewModel constructor(private val pagedComicsByGenreObserver: PagedComicsByGenreObserver,filterViewModel: ComicFilterViewModel):ViewModel(),
ComicFilterViewModel by filterViewModel{
    val comicsList: Flow<PagingData<ViewComics>> = pagedComicsByGenreObserver
        .flowObservable
        .cachedIn(viewModelScope)
    init {
        viewModelScope.launch {
            selectedFilterChip.collectLatest {
                /* Re execute this method whenever the genre changes  */
                fetchComicsByGenreWhenGivenAGenre(it.genres)
            }
        }
       // pagedComicsByGenreObserver(PagedComicsByGenreObserver.PagedComicsByGenreParams(genre =filterViewModel.selectedFilterChip.value.genres, pagingConfig = pagingConfig))
    }
    private fun fetchComicsByGenreWhenGivenAGenre(genre:Genres){
        pagedComicsByGenreObserver(PagedComicsByGenreObserver.PagedComicsByGenreParams(genre, pagingConfig))
    }
    companion object{
        val pagingConfig = PagingConfig(pageSize = 36,
            initialLoadSize = 36)
    }
}
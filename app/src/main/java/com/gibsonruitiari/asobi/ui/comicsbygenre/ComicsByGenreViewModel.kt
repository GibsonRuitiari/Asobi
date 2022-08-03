package com.gibsonruitiari.asobi.ui.comicsbygenre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.domain.bygenre.PagedComicsByGenreObserver
import com.gibsonruitiari.asobi.ui.uiModels.UiGenreModel
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.ui.uiModels.toUiGenreModel
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ComicsByGenreViewModel constructor(private val observer: PagedComicsByGenreObserver,
                                         filterViewModel: ComicFilterViewModel):ViewModel(),
ComicFilterViewModel by filterViewModel{

    @OptIn(ExperimentalCoroutinesApi::class)
    val comicsList: Flow<PagingData<ViewComics>> = currentGenreChoice.flatMapLatest {
        if (it != null) {
            fetchComicsByGenreWhenGivenAGenre(it)
        }
        observer.flowObservable.cachedIn(viewModelScope)
    }
    private fun fetchComicsByGenreWhenGivenAGenre(genre:Genres){
        observer(PagedComicsByGenreObserver.PagedComicsByGenreParams(genre, pagingConfig))
    }
    companion object{
        val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
            enablePlaceholders = false)
    }


}

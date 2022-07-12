package com.gibsonruitiari.asobi.domain.bygenre

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.utils.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreRepo
import com.gibsonruitiari.asobi.domain.interactor.PaginatedEntriesUseCase
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreDataSource
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedComicsByGenreObserver constructor(private
val logger: Logger, private val comicsByGenreRepo: ComicsByGenreRepo
):PaginatedEntriesUseCase<PagedComicsByGenreObserver.PagedComicsByGenreParams,
        ViewComics>() {
    override fun createObservable(params: PagedComicsByGenreParams): Flow<PagingData<ViewComics>> {
        params.genre
        val genreDataSource= ComicsByGenreDataSource(logger,comicsByGenreRepo)
        genreDataSource.setGenre(params.genre)
        return Pager(config = params.pagingConfig, pagingSourceFactory = {genreDataSource}).flow.map {
            value ->
            value.map { sMangaToViewComicMapper(it) }
        }
    }
    data class PagedComicsByGenreParams(val genre:Genres, override val pagingConfig: PagingConfig):PaginatedParams<ViewComics>
}
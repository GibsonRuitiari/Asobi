package com.gibsonruitiari.asobi.domain.popularcomics

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
import com.gibsonruitiari.asobi.domain.interactor.PaginatedEntriesUseCase
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsDataSource
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedPopularComicsObserver  constructor(private val logger: Logger,private val popularComicsRepo: PopularComicsRepo):
    PaginatedEntriesUseCase<PagedPopularComicsObserver.PagedPopularComicsParams,
            ViewComics>() {
    override fun createObservable(params: PagedPopularComicsParams): Flow<PagingData<ViewComics>> {
        return Pager(config = params.pagingConfig,
            pagingSourceFactory = {
                PopularComicsDataSource(logger, popularComicsRepo)
            }
        ).flow.map {
            it.map {manga->
                sMangaToViewComicMapper(manga) }
        }
    }
    data class PagedPopularComicsParams(override val pagingConfig: PagingConfig):
        PaginatedParams<ViewComics>
}
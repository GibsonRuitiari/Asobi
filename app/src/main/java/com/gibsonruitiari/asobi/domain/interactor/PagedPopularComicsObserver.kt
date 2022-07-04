package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.common.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.domain.pagingdatasource.PopularComicsDataSource
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedPopularComicsObserver  constructor(private val logger: Logger):PaginatedEntriesUseCase<PagedPopularComicsObserver.PagedPopularComicsParams,
        ViewComics>() {
    override fun createObservable(params: PagedPopularComicsParams): Flow<PagingData<ViewComics>> {
        return Pager(config = params.pagingConfig,
            pagingSourceFactory = {PopularComicsDataSource(logger)}
        ).flow.map {
            it.map {manga-> sMangaToViewComicMapper(manga) }
        }
    }

    data class PagedPopularComicsParams(override val pagingConfig: PagingConfig): PaginatedParams<ViewComics>
}
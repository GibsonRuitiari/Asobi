package com.gibsonruitiari.asobi.domain.interactor.pagedobservers

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.common.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.domain.interactor.PaginatedEntriesUseCase
import com.gibsonruitiari.asobi.domain.pagingdatasource.LatestComicsDataSource
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedLatestComicsObserver constructor(private val logger: Logger):
    PaginatedEntriesUseCase<PagedLatestComicsObserver.LatestComicsParams,
            ViewComics>() {
    override fun createObservable(params: LatestComicsParams): Flow<PagingData<ViewComics>> {
       return Pager(config =params.pagingConfig,
       pagingSourceFactory = {LatestComicsDataSource(logger)}).flow.map {
           value->
           value.map { sMangaToViewComicMapper(it) }
       }
    }
    data class LatestComicsParams(override val pagingConfig: PagingConfig):
        PaginatedParams<ViewComics>
}
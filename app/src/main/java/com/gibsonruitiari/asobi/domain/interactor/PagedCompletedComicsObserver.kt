package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.common.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.pagingdatasource.CompletedComicsDataSource
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedCompletedComicsObserver constructor(private val logger: Logger):PaginatedEntriesUseCase<PagedCompletedComicsObserver.CompletedComicsParams,
        ViewComics>() {
    override fun createObservable(params: CompletedComicsParams): Flow<PagingData<ViewComics>> {
        return Pager(config = params.pagingConfig,
        pagingSourceFactory = {CompletedComicsDataSource(logger)}).flow.map {
            value ->
            value.map { sMangaToViewComicMapper(it) }
        }
    }
    data class CompletedComicsParams(override val pagingConfig: PagingConfig): PaginatedParams<ViewComics>
}
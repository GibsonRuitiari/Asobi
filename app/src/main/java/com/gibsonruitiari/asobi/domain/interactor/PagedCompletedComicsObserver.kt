package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.pagingdatasource.CompletedComicsDataSource
import kotlinx.coroutines.flow.Flow

class PagedCompletedComicsObserver constructor(private val logger: Logger):PaginatedEntriesUseCase<PagedCompletedComicsObserver.CompletedComicsParams,
        SManga>() {
    override fun createObservable(params: CompletedComicsParams): Flow<PagingData<SManga>> {
        return Pager(config = params.pagingConfig,
        pagingSourceFactory = {CompletedComicsDataSource(logger)}).flow
    }
    data class CompletedComicsParams(override val pagingConfig: PagingConfig):PaginatedEntriesUseCase.PaginatedParams<SManga>
}
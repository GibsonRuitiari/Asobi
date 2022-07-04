package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.*
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.pagingdatasource.PopularComicsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PagedPopularComicsObserver  constructor(private val logger: Logger):PaginatedEntriesUseCase<PagedPopularComicsObserver.PagedPopularComicsParams, SManga>() {
    override fun createObservable(params: PagedPopularComicsParams): Flow<PagingData<SManga>> {
        return Pager(config = params.pagingConfig,
            pagingSourceFactory = {PopularComicsDataSource(logger)}
        ).flow
    }

    data class PagedPopularComicsParams(override val pagingConfig: PagingConfig): PaginatedParams<SManga>
}
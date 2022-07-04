package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.pagingdatasource.LatestComicsDataSource
import kotlinx.coroutines.flow.Flow

class PagedLatestComicsObserver constructor(private val logger: Logger):PaginatedEntriesUseCase<PagedLatestComicsObserver.LatestComicsParams,
        SManga>() {
    override fun createObservable(params: LatestComicsParams): Flow<PagingData<SManga>> {
       return Pager(config =params.pagingConfig,
       pagingSourceFactory = {LatestComicsDataSource(logger)}).flow
    }
    data class LatestComicsParams(override val pagingConfig: PagingConfig):PaginatedParams<SManga>
}
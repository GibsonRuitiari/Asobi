package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.pagingdatasource.OngoingComicsDataSource
import kotlinx.coroutines.flow.Flow

class PagedOngoingComicsObserver constructor(private val logger: Logger):PaginatedEntriesUseCase<PagedOngoingComicsObserver.OngoingComicsParams,
SManga>(){
    override fun createObservable(params: OngoingComicsParams): Flow<PagingData<SManga>> {
        return Pager(config = params.pagingConfig,
        pagingSourceFactory = {OngoingComicsDataSource(logger)}).flow
    }
    data class OngoingComicsParams(override val pagingConfig: PagingConfig):PaginatedParams<SManga>
}
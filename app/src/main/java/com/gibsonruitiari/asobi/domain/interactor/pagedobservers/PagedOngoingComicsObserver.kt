package com.gibsonruitiari.asobi.domain.interactor.pagedobservers

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.common.utils.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.OngoingComicsRepo
import com.gibsonruitiari.asobi.domain.interactor.PaginatedEntriesUseCase
import com.gibsonruitiari.asobi.domain.pagingdatasource.OngoingComicsDataSource
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedOngoingComicsObserver constructor(private val logger: Logger,
private val ongoingComicsRepo: OngoingComicsRepo):PaginatedEntriesUseCase<PagedOngoingComicsObserver.OngoingComicsParams,ViewComics>(){
    override fun createObservable(params: OngoingComicsParams): Flow<PagingData<ViewComics>> {
        return Pager(config = params.pagingConfig,
        pagingSourceFactory = {OngoingComicsDataSource(logger, ongoingComicsRepo)}).flow.map {value: PagingData<SManga> ->
            value.map { sMangaToViewComicMapper(it) }
        }
    }
    data class OngoingComicsParams(override val pagingConfig: PagingConfig):
        PaginatedParams<ViewComics>
}
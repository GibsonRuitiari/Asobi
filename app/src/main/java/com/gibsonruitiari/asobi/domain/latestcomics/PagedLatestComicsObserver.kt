package com.gibsonruitiari.asobi.domain.latestcomics

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.domain.PaginatedEntriesUseCase
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsDataSource
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedLatestComicsObserver constructor(private val logger: Logger, private val latestComicsRepo: LatestComicsRepo):
    PaginatedEntriesUseCase<PagedLatestComicsObserver.LatestComicsParams,
            ViewComics>() {
    override fun createObservable(params: LatestComicsParams): Flow<PagingData<ViewComics>> {
       return Pager(config =params.pagingConfig,
       pagingSourceFactory = { LatestComicsDataSource(logger, latestComicsRepo) }).flow.map {
           value->
           value.map {
               sMangaToViewComicMapper(it)
           }
       }
    }
    data class LatestComicsParams(override val pagingConfig: PagingConfig):
        PaginatedParams<ViewComics>
}
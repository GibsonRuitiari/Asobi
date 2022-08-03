package com.gibsonruitiari.asobi.domain.completedcomics

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import com.gibsonruitiari.asobi.domain.PaginatedEntriesUseCase
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsDataSource
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagedCompletedComicsObserver constructor(private val logger: Logger, private val completedComicsRepo: CompletedComicsRepo):
    PaginatedEntriesUseCase<PagedCompletedComicsObserver.CompletedComicsParams,
            ViewComics>() {
    override fun createObservable(params: CompletedComicsParams): Flow<PagingData<ViewComics>> {
        return Pager(config = params.pagingConfig,
        pagingSourceFactory = { CompletedComicsDataSource(logger, completedComicsRepo) }).flow.map {
            value ->
            value.map { sMangaToViewComicMapper(it) }
        }
    }
    data class CompletedComicsParams(override val pagingConfig: PagingConfig):
        PaginatedParams<ViewComics>
}
package com.gibsonruitiari.asobi.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibsonruitiari.asobi.common.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.domain.pagingdatasource.CompletedComicsDataSource
import com.gibsonruitiari.asobi.domain.pagingdatasource.LatestComicsDataSource
import com.gibsonruitiari.asobi.domain.pagingdatasource.OngoingComicsDataSource
import com.gibsonruitiari.asobi.domain.pagingdatasource.PopularComicsDataSource
import com.gibsonruitiari.asobi.presenter.discoveruicontract.DiscoverComicsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DiscoverComicsUseCase constructor(private val latestComicsDataSource: LatestComicsDataSource,
                                        private val popularComicsDataSource: PopularComicsDataSource,
                                        private val ongoingComicsDataSource: OngoingComicsDataSource,
                                        private val completedComicsDataSource: CompletedComicsDataSource):FlowUseCase<Unit,
DiscoverComicsResult>(){
    private val latestComicsPagingConfig = PagingConfig(pageSize = 36, prefetchDistance = 20)
    private val latestComicsPager = Pager(config = latestComicsPagingConfig, pagingSourceFactory = {latestComicsDataSource}).flow

    private val popularComicsPagingConfig = PagingConfig(pageSize = 36, prefetchDistance = 20)
    private val popularComicsPager = Pager(config = popularComicsPagingConfig, pagingSourceFactory = {popularComicsDataSource}).flow

    private val ongoingComicsPagingConfig = PagingConfig(pageSize = 36, prefetchDistance = 20)
    private val ongoingComicsPager = Pager(config = ongoingComicsPagingConfig, pagingSourceFactory = {ongoingComicsDataSource}).flow

    private val completedComicsPagingConfig = PagingConfig(pageSize = 36, prefetchDistance = 20)
    private val completedComicsPager = Pager(config = completedComicsPagingConfig, pagingSourceFactory = {completedComicsDataSource}).flow

    override fun run(params: Unit): Flow<DiscoverComicsResult> = flow {
        val latestComics=latestComicsPager.map { pagingData-> pagingData.map {comic-> sMangaToViewComicMapper.map(comic) } }
        val popularComics = popularComicsPager.map { value: PagingData<SManga> -> value.map { comic-> sMangaToViewComicMapper.map(comic) }  }
        val onGoingComics = ongoingComicsPager.map { value: PagingData<SManga> ->  value.map { comic-> sMangaToViewComicMapper.map(comic) }}
        val completedComics = completedComicsPager.map { value: PagingData<SManga> ->value.map { comic->
            sMangaToViewComicMapper.map(comic)
        }  }
        DiscoverComicsResult(latestComics = DiscoverComicsResult.DiscoverComicsData(comicsData = latestComics),
       popularComics =DiscoverComicsResult.DiscoverComicsData(popularComics), ongoingComics = DiscoverComicsResult.DiscoverComicsData(onGoingComics),
            completedComics = DiscoverComicsResult.DiscoverComicsData(completedComics))
    }
}
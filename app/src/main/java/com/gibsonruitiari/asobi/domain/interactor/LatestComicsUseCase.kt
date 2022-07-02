package com.gibsonruitiari.asobi.domain.interactor

import com.gibsonruitiari.asobi.domain.pagingdatasource.CompletedComicsDataSource
import com.gibsonruitiari.asobi.domain.pagingdatasource.LatestComicsDataSource
import com.gibsonruitiari.asobi.domain.pagingdatasource.OngoingComicsDataSource
import com.gibsonruitiari.asobi.domain.pagingdatasource.PopularComicsDataSource
import com.gibsonruitiari.asobi.presenter.discoveruicontract.DiscoverComicsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LatestComicsUseCase constructor(private val latestComicsDataSource: LatestComicsDataSource,
private val popularComicsDataSource: PopularComicsDataSource,
private val ongoingComicsDataSource: OngoingComicsDataSource,
private val completedComicsDataSource: CompletedComicsDataSource):FlowUseCase<Unit,
DiscoverComicsResult>(){

    override fun run(params: Unit): Flow<DiscoverComicsResult> = flow {
        popularComicsDataSource
     //   DiscoverComicsResult()
    }
}
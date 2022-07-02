package com.gibsonruitiari.asobi.domain.interactor

import com.gibsonruitiari.asobi.domain.pagingdatasource.LatestComicsDataSource
import com.gibsonruitiari.asobi.presenter.discoveruicontract.DiscoverComicsResult
import kotlinx.coroutines.flow.Flow

class LatestComicsUseCase constructor(private val latestComicsDataSource: LatestComicsDataSource):FlowUseCase<Unit,
DiscoverComicsResult>(){
    override fun run(params: Unit): Flow<DiscoverComicsResult> {
        TODO("Not yet implemented")
    }
}
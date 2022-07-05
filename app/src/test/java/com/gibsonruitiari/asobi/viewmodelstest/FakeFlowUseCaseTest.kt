package com.gibsonruitiari.asobi.viewmodelstest

import com.gibsonruitiari.asobi.domain.interactor.FlowUseCase
import com.gibsonruitiari.asobi.listOfComics
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDiscoverComicsFlowUseCaseTest:FlowUseCase<FakeDiscoverComicsFlowUseCaseTest.Params, DiscoverComicsResult>() {
    override fun run(params: Params): Flow<DiscoverComicsResult> = flow {
        val discoverComicsData=DiscoverComicsResult.DiscoverComicsData(isLoading = params.isLoading,
        errorMessage = params.errorMessage,
        comicsData = listOfComics)
        emit(DiscoverComicsResult(latestComics = discoverComicsData,
        ongoingComics = discoverComicsData, completedComics = discoverComicsData,
        popularComics = discoverComicsData))
    }
    data class Params(val isLoading:Boolean=false,val
    errorMessage:String?=null)
}
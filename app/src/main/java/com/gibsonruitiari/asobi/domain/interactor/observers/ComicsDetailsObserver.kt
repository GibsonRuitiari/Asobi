package com.gibsonruitiari.asobi.domain.interactor.observers

import com.gibsonruitiari.asobi.common.sMangaDetailsToViewComicDetails
import com.gibsonruitiari.asobi.common.utils.toNetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.repositories.ComicsDetailsRepo
import com.gibsonruitiari.asobi.domain.interactor.FlowUseCase
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicsDetailsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ComicsDetailsObserver constructor(private val comicsDetailsRepo: ComicsDetailsRepo):FlowUseCase<ComicsDetailsObserver.ComicDetailsParams, ComicsDetailsResult>(){
    override fun run(params: ComicDetailsParams): Flow<ComicsDetailsResult>  {
      return comicsDetailsRepo.getComicDetails(params.url).map { sMangaDetailsToViewComicDetails(it) }.toNetworkResource().map {
           val viewComicDetails=ComicsDetailsResult.ComicDetails(errorMessage = it.throwable?.message,isLoading = it.status==Status.LOADING,viewComicDetails = it.data)
           ComicsDetailsResult(viewComicDetails) }

    }

    data class ComicDetailsParams(val url:String)
}
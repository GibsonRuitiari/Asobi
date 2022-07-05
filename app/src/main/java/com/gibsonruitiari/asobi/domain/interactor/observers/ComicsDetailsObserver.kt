package com.gibsonruitiari.asobi.domain.interactor.observers

import com.gibsonruitiari.asobi.common.sMangaDetailsToViewComicDetails
import com.gibsonruitiari.asobi.data.comicDetails
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.data.network.NetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.domain.interactor.FlowUseCase
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComicDetails
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicsDetailsResult
import kotlinx.coroutines.flow.*

class ComicsDetailsObserver :FlowUseCase<ComicsDetailsObserver.ComicDetailsParams, ComicsDetailsResult>(){
    override fun run(params: ComicDetailsParams): Flow<ComicsDetailsResult> {
      return comicDetails(params.url).toNetworkResource().map {
           val viewComicDetails=ComicsDetailsResult.ComicDetails(errorMessage = it.throwable?.message,isLoading = it.status==Status.LOADING,viewComicDetails = it.data)
           ComicsDetailsResult(viewComicDetails)
       }
    }

    private fun Flow<SMangaInfo>.toNetworkResource():Flow<NetworkResource<ViewComicDetails>> = flow {
        map { sMangaDetailsToViewComicDetails(it) }
            .onStart { emit(NetworkResource.loading(null)) }
            .catch { emit(NetworkResource.error(it,null)) }
            .collect{emit(NetworkResource.success(it))}
    }
    data class ComicDetailsParams(val url:String)
}
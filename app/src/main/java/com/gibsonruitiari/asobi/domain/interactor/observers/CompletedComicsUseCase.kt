package com.gibsonruitiari.asobi.domain.interactor.observers

import com.gibsonruitiari.asobi.common.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.data.completedComics
import com.gibsonruitiari.asobi.data.datamodels.MangaPage
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.network.NetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.domain.interactor.FlowUseCase
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsResult
import kotlinx.coroutines.flow.*

class CompletedComicsUseCase:FlowUseCase<CompletedComicsUseCase.CompletedComicsParams, DiscoverComicsResult>() {
    override fun run(params: CompletedComicsParams): Flow<DiscoverComicsResult> = flow  {
     completedComics(1).toNetworkResource()
    }

    data class CompletedComicsParams(val itemsSize:Int)
    private fun Flow<NetworkResource<List<SManga>>>.toComicsResultData() =map{
        DiscoverComicsResult.DiscoverComicsData(isLoading = it.status == Status.LOADING,
        comicsData =it.data?.map { sMangaToViewComicMapper(it) } ?: emptyList(),
            errorMessage = it.throwable?.message)
    }
    private fun Flow<MangaPage>.toNetworkResource():Flow<NetworkResource<List<SManga>>> = flow {
          onStart { emit(NetworkResource.loading(null)) }
          catch { emit(NetworkResource.error(it,null)) }
          collect{ emit(NetworkResource.success(it.mangas)) }
    }
}
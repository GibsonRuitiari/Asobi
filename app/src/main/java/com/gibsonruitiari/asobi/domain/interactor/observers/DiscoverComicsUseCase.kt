package com.gibsonruitiari.asobi.domain.interactor.observers

import com.gibsonruitiari.asobi.common.utils.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.common.utils.toNetworkResource
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.network.NetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.repositories.CompletedComicsRepo
import com.gibsonruitiari.asobi.data.repositories.LatestComicsRepo
import com.gibsonruitiari.asobi.data.repositories.OngoingComicsRepo
import com.gibsonruitiari.asobi.data.repositories.PopularComicsRepo
import com.gibsonruitiari.asobi.domain.interactor.FlowUseCase
import com.gibsonruitiari.asobi.presenter.uicontracts.DiscoverComicsResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.map

class DiscoverComicsUseCase constructor(private val latestComicsRepo: LatestComicsRepo,
private val ongoingComicsRepo: OngoingComicsRepo,
private val popularComicsRepo: PopularComicsRepo,
private val completedComicsRepo: CompletedComicsRepo):FlowUseCase<DiscoverComicsUseCase.DiscoverComicsParams, DiscoverComicsResult>() {
    override fun run(params: DiscoverComicsParams): Flow<DiscoverComicsResult> = combine(
        completedComicsRepo
        .getCompletedComics(params.page)
        .take(params.itemsSize)
        .toNetworkResource()
        .toComicsResultData(),
              latestComicsRepo.getLatestComics(params.page)
                  .take(params.itemsSize)
                  .toNetworkResource()
                  .toComicsResultData(),
              popularComicsRepo.getPopularComics(params.page)
                  .take(params.itemsSize)
                  .toNetworkResource()
                  .toComicsResultData(),
              ongoingComicsRepo.getOngoingComics(params.page)
                  .take(params.itemsSize)
                  .toNetworkResource()
                  .toComicsResultData()){
          completed,latest,popular,ongoing->
             DiscoverComicsResult(latestComics = latest, completedComics = completed,
             popularComics = popular, ongoingComics = ongoing)
      }
    data class DiscoverComicsParams(val itemsSize:Int,val page:Int)
    private fun Flow<NetworkResource<List<SManga>>>.toComicsResultData() =map{
        DiscoverComicsResult.DiscoverComicsData(isLoading = it.status == Status.LOADING,
        comicsData =it.data?.map { sMangaToViewComicMapper(it)
        } ?: emptyList(),
            errorMessage = it.throwable?.message)
    }

}
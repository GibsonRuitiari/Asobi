package com.gibsonruitiari.asobi.domain

import com.gibsonruitiari.asobi.utilities.utils.toNetworkResource
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.network.NetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverComicsResult
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.map

class DiscoverComicsUseCase constructor(private val latestComicsRepo: LatestComicsRepo,
                                        private val ongoingComicsRepo: OngoingComicsRepo,
                                        private val popularComicsRepo: PopularComicsRepo,
                                        private val completedComicsRepo: CompletedComicsRepo
): FlowUseCase<DiscoverComicsUseCase.DiscoverComicsParams, DiscoverComicsResult>() {
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
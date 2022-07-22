package com.gibsonruitiari.asobi.domain.searchcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.network.NetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.shared.searchcomics.SearchComicsRepo
import com.gibsonruitiari.asobi.domain.FlowUseCase
import com.gibsonruitiari.asobi.ui.comicssearch.SearchComicsResult
import com.gibsonruitiari.asobi.utilities.extensions.parseThrowableErrorMessageIntoUsefulMessage
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.utilities.toNetworkResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchComicsUseCase constructor(private val searchComicsRepo: SearchComicsRepo,
private val logger: Logger):FlowUseCase<SearchComicsUseCase.SearchComicsUseCaseParams,
        SearchComicsResult>() {
    override fun run(params: SearchComicsUseCaseParams): Flow<SearchComicsResult> {
        logger.i("search param is ${params.searchTerm}")
        return searchComicsRepo.searchForComicWhenGivenASearchTerm(params.searchTerm)
            .toNetworkResource()
            .toSearchComicsResultData()
    }
    private fun Flow<NetworkResource<List<SManga>>>.toSearchComicsResultData()= map {
        SearchComicsResult(isLoading = it.status == Status.LOADING,
        searchResult =  it.data?.map { sManga -> sMangaToViewComicMapper(sManga) } ?: emptyList(),
        errorMessage = it.throwable?.parseThrowableErrorMessageIntoUsefulMessage())
    }
    data class SearchComicsUseCaseParams(val searchTerm:String)
}
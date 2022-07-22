package com.gibsonruitiari.asobi.domain.comicdetails

import com.gibsonruitiari.asobi.utilities.toNetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.shared.comicdetails.ComicsDetailsRepo
import com.gibsonruitiari.asobi.domain.FlowUseCase
import com.gibsonruitiari.asobi.ui.comicdetails.ComicsDetailsResult
import com.gibsonruitiari.asobi.utilities.sMangaDetailsToViewComicDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ComicsDetailsObserver constructor(private val comicsDetailsRepo: ComicsDetailsRepo):
    FlowUseCase<ComicsDetailsObserver.ComicDetailsParams, ComicsDetailsResult>(){
    override fun run(params: ComicDetailsParams): Flow<ComicsDetailsResult>  {
      return comicsDetailsRepo.getComicDetails(params.url).map {
          sMangaDetailsToViewComicDetails(it) }.toNetworkResource().map {
           val viewComicDetails= ComicsDetailsResult.ComicDetails(errorMessage = it.throwable?.message,isLoading = it.status==Status.LOADING,viewComicDetails = it.data)
           ComicsDetailsResult(viewComicDetails) }
    }
    data class ComicDetailsParams(val url:String)
}
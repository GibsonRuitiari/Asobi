package com.gibsonruitiari.asobi.domain.interactor

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.popularComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class ObserveCompletedComics:SubjectInteractor<ObserveCompletedComics.Params,List<SManga>>() {

    override fun createObservable(params: Params): Flow<List<SManga>> = flow{
        val homePagePopularComics= popularComics(1).firstOrNull()?.mangas ?: emptyList()
        emit(homePagePopularComics.take(params.count))
    }
    data class Params(val count:Int=20) // number of items to be displayed in home screen
}

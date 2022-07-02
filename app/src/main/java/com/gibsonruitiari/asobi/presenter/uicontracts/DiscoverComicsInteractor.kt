package com.gibsonruitiari.asobi.presenter.uicontracts

import androidx.paging.PagingData
import com.gibsonruitiari.asobi.common.Action
import com.gibsonruitiari.asobi.common.Effect
import com.gibsonruitiari.asobi.common.State
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class DiscoverComicsState(val comicsData:DiscoverComicsResult):State{
    companion object{
        val Empty = DiscoverComicsState(DiscoverComicsResult.EMPTY)
    }
}
sealed class DiscoverComicsAction: Action {
    object LoadComics:DiscoverComicsAction()
    data class Error(val message:String=""):DiscoverComicsAction()
}

sealed class DiscoverComicsSideEffect: Effect {
    data class Error(val message: String=""):DiscoverComicsSideEffect()
}
data class DiscoverComicsResult(val latestComics:DiscoverComicsData,
val popularComics:DiscoverComicsData,val ongoingComics:DiscoverComicsData,
val completedComics:DiscoverComicsData){
    companion object{
        val EMPTY = DiscoverComicsResult(DiscoverComicsData.EMPTY,
            DiscoverComicsData.EMPTY,DiscoverComicsData.EMPTY,DiscoverComicsData.EMPTY)
    }
    data class DiscoverComicsData(val comicsData:Flow<PagingData<ViewComics>>,
    ){
        companion object{
            val EMPTY = DiscoverComicsData(comicsData = emptyFlow())
        }
    }
}
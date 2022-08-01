package com.gibsonruitiari.asobi.ui.discovercomics

import com.gibsonruitiari.asobi.utilities.Action
import com.gibsonruitiari.asobi.utilities.Effect
import com.gibsonruitiari.asobi.utilities.State
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics

data class DiscoverComicsState(val isLoading: Boolean,val comicsData: DiscoverComicsResult):State{
    companion object{
        val Empty = DiscoverComicsState(false, DiscoverComicsResult.EMPTY)
    }
}
sealed class DiscoverComicsAction: Action {
    object LoadComics: DiscoverComicsAction()
    data class Error(val message:String=""): DiscoverComicsAction()
}

sealed class DiscoverComicsSideEffect: Effect {
    data class Error(val message: String=""): DiscoverComicsSideEffect()
}
data class DiscoverComicsResult(val latestComics: DiscoverComicsData,
                                val popularComics: DiscoverComicsData, val ongoingComics: DiscoverComicsData,
                                val completedComics: DiscoverComicsData,val marvelComics:DiscoverComicsData,val dcComics:DiscoverComicsData
){
    companion object{
        val EMPTY = DiscoverComicsResult(
            DiscoverComicsData.EMPTY,
            DiscoverComicsData.EMPTY, DiscoverComicsData.EMPTY, DiscoverComicsData.EMPTY, DiscoverComicsData.EMPTY,DiscoverComicsData.EMPTY
        )
    }
    data class DiscoverComicsData(val isLoading:Boolean=false,

                                  val comicsData:List<ViewComics>,val errorMessage:String?,
    ){
        companion object{
            val EMPTY = DiscoverComicsData(false,comicsData = emptyList(),null)
        }
    }
}
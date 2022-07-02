package com.gibsonruitiari.asobi.presenter.discoveruicontract

import androidx.paging.PagingData
import com.gibsonruitiari.asobi.common.Action
import com.gibsonruitiari.asobi.common.Effect
import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

sealed class DiscoverComicsAction: Action {
    object LoadComics:DiscoverComicsAction()
    data class Error(val message:String=""):DiscoverComicsAction()
}

sealed class DiscoverComicsSideEffect: Effect {
    data class Error(val message: String=""):DiscoverComicsSideEffect()
}
class DiscoverComicsResult{
    companion object{

    }
    data class DiscoverComicsData(val comicsData:Flow<PagingData<SManga>>)
}
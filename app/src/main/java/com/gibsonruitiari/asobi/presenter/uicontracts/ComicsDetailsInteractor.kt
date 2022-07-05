package com.gibsonruitiari.asobi.presenter.uicontracts

import com.gibsonruitiari.asobi.common.Action
import com.gibsonruitiari.asobi.common.Effect
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComicDetails


data class ComicDetailsState(val isLoading: Boolean,
val comicsDetailsResult: ComicsDetailsResult){
    companion object{
        val Empty=ComicDetailsState(false,ComicsDetailsResult.empty)
    }
}
sealed class ComicDetailsAction:Action{
    object LoadComicDetails:ComicDetailsAction()
    data class Error(val message: String=""):ComicDetailsAction()
}
sealed class ComicDetailsSideEffect:Effect{
    data class Error(val message:String=""):ComicDetailsSideEffect()
}
data class ComicsDetailsResult(val comicDetails:ComicDetails){
    companion object{
        val empty = ComicsDetailsResult(ComicDetails.EMPTY)
    }
    data class ComicDetails(val isLoading:Boolean=false,
    val viewComicDetails: ViewComicDetails?,
    val errorMessage:String?){
        companion object{
            val EMPTY = ComicDetails(isLoading = false, errorMessage = null,viewComicDetails = null)
        }
    }
}
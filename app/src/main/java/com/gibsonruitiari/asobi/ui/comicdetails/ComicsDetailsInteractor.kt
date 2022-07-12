package com.gibsonruitiari.asobi.ui.comicdetails

import com.gibsonruitiari.asobi.utilities.Action
import com.gibsonruitiari.asobi.utilities.Effect
import com.gibsonruitiari.asobi.utilities.State
import com.gibsonruitiari.asobi.ui.uiModels.ViewComicDetails


data class ComicDetailsState(val isLoading: Boolean,
val comicsDetailsResult: ComicsDetailsResult
):State{
    companion object{
        val Empty= ComicDetailsState(false, ComicsDetailsResult.empty)
    }
}
sealed class ComicDetailsAction:Action{
    object LoadComicDetails: ComicDetailsAction()
    data class Error(val message: String=""): ComicDetailsAction()
}
sealed class ComicDetailsSideEffect:Effect{
    data class Error(val message:String=""): ComicDetailsSideEffect()
}
data class ComicsDetailsResult(val comicDetails: ComicDetails){
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
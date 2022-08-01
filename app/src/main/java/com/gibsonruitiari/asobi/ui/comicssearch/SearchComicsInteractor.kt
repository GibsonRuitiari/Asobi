package com.gibsonruitiari.asobi.ui.comicssearch

import com.gibsonruitiari.asobi.ui.uiModels.UiGenreModel
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.Action
import com.gibsonruitiari.asobi.utilities.Effect
import com.gibsonruitiari.asobi.utilities.State

data class SearchComicsState(val isLoading:Boolean,
val searchResults:SearchComicsResult):State{
    companion object{
        private val defaultSearchResult = SearchComicsResult.empty
        val empty = SearchComicsState(isLoading = defaultSearchResult.isLoading,
        searchResults = defaultSearchResult)
    }
}

sealed class SearchComicsAction:Action{
    object ExecuteSearch:SearchComicsAction()
    data class Error(val message:String=""):SearchComicsAction()
}
sealed class SearchComicsSideEffect:Effect{
    data class Error(val message: String=""):SearchComicsSideEffect()
}
data class SearchComicsResult(val searchResults:List<ViewComics>,
                              val isLoading:Boolean=false, val errorMessage:String?){
    companion object{
        val empty = SearchComicsResult(listOf(),false,null)
    }
}
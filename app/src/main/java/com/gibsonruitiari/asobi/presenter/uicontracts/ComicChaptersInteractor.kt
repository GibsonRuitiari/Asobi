package com.gibsonruitiari.asobi.presenter.uicontracts

import com.gibsonruitiari.asobi.common.Action
import com.gibsonruitiari.asobi.common.Effect
import com.gibsonruitiari.asobi.common.State
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComicPage


data class ComicChapterState(val isChapterLoading:Boolean,
val comicChapterResult:ComicChapterResult):State{
    companion object{
        val empty = ComicChapterState(false, ComicChapterResult.empty)
    }
}
sealed class ComicChaptersSideEffect:Effect{
    data class Error(val message:String=""):ComicChaptersSideEffect()
}
sealed class ComicChapterAction:Action{
    object LoadComicChapter:ComicChapterAction()
    data class Error(val message: String=""):ComicChapterAction()
}
data class ComicChapterResult(val comicChapterData:ComicChapterPages){
    companion object{
        val empty = ComicChapterResult(ComicChapterPages.EMPTY)
    }
    data class ComicChapterPages(val arePagesLoading:Boolean=false,
                                 val errorMessage:String?,
                                 val totalNumberOfPages:Int?,
    val comicPages:List<ViewComicPage>){
        companion object{
            val EMPTY = ComicChapterPages(arePagesLoading = false,
            errorMessage = null, comicPages = emptyList(), totalNumberOfPages = null)
        }
    }
}
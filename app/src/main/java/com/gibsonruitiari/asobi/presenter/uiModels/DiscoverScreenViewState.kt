package com.gibsonruitiari.asobi.presenter.uiModels

import com.gibsonruitiari.asobi.common.UiMessageReceiver

data class DiscoverScreenViewState (val latestComics:List<ViewComics> = emptyList(),
val latestComicsRefreshing:Boolean=false,
val popularComics:List<ViewComics> = emptyList(),
val popularComicsRefreshing:Boolean=false,
val ongoingComics:List<ViewComics> = emptyList(),
val ongoingComicsRefreshing:Boolean=false,val completedComics:List<ViewComics> = emptyList(),
                                    val completedComicsRefreshing:Boolean=false,
val message:UiMessageReceiver?=null){
    companion object{
        val emptyState = DiscoverScreenViewState()
    }
    val refreshing:Boolean
    get() = latestComicsRefreshing || popularComicsRefreshing||ongoingComicsRefreshing||completedComicsRefreshing
}
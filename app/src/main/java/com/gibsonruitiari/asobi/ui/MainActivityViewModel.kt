package com.gibsonruitiari.asobi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivityViewModel:ViewModel() {
  private val mainFragmentNavigationEvents = Channel<MainFragmentNavigationAction>(CONFLATED)
    private val _comicsSearchScreenNavigationEvents=Channel<SearchScreenNavigationAction>(CONFLATED)
    private var navigationJob:Job?=null
    override fun onCleared() {
        super.onCleared()
        // sanity check just in case
        if (navigationJob!=null) navigationJob=null
    }
    // only one observer should receive the updates
   val navigationEvents:Flow<MainFragmentNavigationAction> = mainFragmentNavigationEvents.receiveAsFlow()
   val comicsSearchScreenNavigationEvents =_comicsSearchScreenNavigationEvents.receiveAsFlow()

    private inline fun navigateTo(crossinline navigationAction:()->Unit){
        // don't trigger navigation if there is a current navigation job ongoing
        if (navigationJob!=null) return
        navigationJob = viewModelScope.launch {
            try {
                navigationAction.invoke()
            }catch (ex:Exception){
                // ignore error
            }finally {
                navigationJob=null
            }
        }
    }

    fun openComicsGenreScreenFromSearchScreen(){
        navigateTo{ _comicsSearchScreenNavigationEvents.trySend(SearchScreenNavigationAction.NavigateToComicsGenreScreen) }
    }
    fun openComicsByGenreScreenFromSearchScreen(){
        navigateTo { _comicsSearchScreenNavigationEvents.trySend(SearchScreenNavigationAction.NavigateToComicsByGenreFragmentScreen) }
    }
    fun openComicsSearchResultsScreen(){
         _comicsSearchScreenNavigationEvents.trySend(SearchScreenNavigationAction.NavigateToComicsSearchResultScreen)
    }
    fun openDiscoverScreen(){
        navigateTo { mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToDiscoverScreen) }
    }
    fun openLatestComicsScreen(){
        navigateTo { mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToLatestComicsScreen) }
    }
    fun openOngoingComicsScreen(){
        navigateTo { mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToOngoingComicsScreen) }
    }
    fun openCompletedComicsScreen(){
        navigateTo { mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToCompletedComicsScreen) }
    }
    fun openComicsByGenreScreen(){
        navigateTo {  mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToComicsByGenreScreen) }
    }
    fun openPopularComicsScreen(){
        navigateTo { mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToPopularComicsScreen) }
    }
}
sealed class SearchScreenNavigationAction{
    object NavigateToComicsGenreScreen:SearchScreenNavigationAction()
    object NavigateToComicsByGenreFragmentScreen:SearchScreenNavigationAction()
    object NavigateToComicsSearchResultScreen:SearchScreenNavigationAction()
}
sealed class MainFragmentNavigationAction{
    object NavigateToDiscoverScreen:MainFragmentNavigationAction()
    object NavigateToLatestComicsScreen:MainFragmentNavigationAction()
    object NavigateToOngoingComicsScreen:MainFragmentNavigationAction()
    object NavigateToPopularComicsScreen:MainFragmentNavigationAction()
    object NavigateToComicsByGenreScreen:MainFragmentNavigationAction()
    object NavigateToCompletedComicsScreen:MainFragmentNavigationAction()
}

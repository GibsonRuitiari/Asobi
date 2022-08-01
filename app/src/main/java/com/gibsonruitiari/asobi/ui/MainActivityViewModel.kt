package com.gibsonruitiari.asobi.ui

import androidx.lifecycle.ViewModel
import com.gibsonruitiari.asobi.utilities.extensions.tryOffer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.*

class MainActivityViewModel:ViewModel() {
  private val mainFragmentNavigationEvents = Channel<MainFragmentNavigationAction>(CONFLATED)

    // only one observer should receive the updates
   val navigationEvents:Flow<MainFragmentNavigationAction> = mainFragmentNavigationEvents.receiveAsFlow()

    fun openDiscoverScreen(){
        mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToDiscoverScreen)
    }
    fun openLatestComicsScreen(){
        mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToLatestComicsScreen)
    }
    fun openOngoingComicsScreen(){
        mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToOngoingComicsScreen)
    }
    fun openCompletedComicsScreen(){
        mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToCompletedComicsScreen)
    }
    fun openComicsByGenreScreen(){
        mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToComicsByGenreScreen)
    }
    fun openPopularComicsScreen(){
        mainFragmentNavigationEvents.trySend(MainFragmentNavigationAction.NavigateToPopularComicsScreen)
    }

}
sealed class MainFragmentNavigationAction{
    object NavigateToDiscoverScreen:MainFragmentNavigationAction()
    object NavigateToLatestComicsScreen:MainFragmentNavigationAction()
    object NavigateToOngoingComicsScreen:MainFragmentNavigationAction()
    object NavigateToPopularComicsScreen:MainFragmentNavigationAction()
    object NavigateToComicsByGenreScreen:MainFragmentNavigationAction()
    object NavigateToCompletedComicsScreen:MainFragmentNavigationAction()
}

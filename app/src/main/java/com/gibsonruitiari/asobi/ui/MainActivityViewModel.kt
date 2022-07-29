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

}
sealed class MainFragmentNavigationAction{
    object NavigateToDiscoverScreen:MainFragmentNavigationAction()
    object NavigateToLatestComicsScreen:MainFragmentNavigationAction()
    object NavigateToOngoingComicsScreen:MainFragmentNavigationAction()
}

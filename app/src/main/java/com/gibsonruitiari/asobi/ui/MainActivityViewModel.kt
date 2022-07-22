package com.gibsonruitiari.asobi.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.receiveAsFlow

class MainActivityViewModel:ViewModel() {
  private val _navigationActions = Channel<MainActivityNavigationAction>(Channel.CONFLATED)
  val navigationActions = _navigationActions.receiveAsFlow()
    fun openSearchScreen(){
        _navigationActions.tryOffer(MainActivityNavigationAction.NavigateToSearchScreen)
    }
    fun openDiscoverScreen(){
        _navigationActions.tryOffer(MainActivityNavigationAction.NavigateDiscoverScreen)
    }
    fun <E> SendChannel<E>.tryOffer(element:E):Boolean = try{
        trySend(element).isSuccess
    }catch (t:Throwable) {
        false
    }
}
sealed class MainActivityNavigationAction{
    object NavigateToSearchScreen:MainActivityNavigationAction()
    object NavigateDiscoverScreen:MainActivityNavigationAction()
}
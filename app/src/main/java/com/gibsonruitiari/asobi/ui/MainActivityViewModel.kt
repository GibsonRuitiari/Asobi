package com.gibsonruitiari.asobi.ui

import androidx.lifecycle.ViewModel
import com.gibsonruitiari.asobi.utilities.extensions.tryOffer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class MainActivityViewModel:ViewModel() {
  private val mainFragmentNavigationEvents = Channel<MainFragmentNavigationAction>(Channel.CONFLATED)
    private val _isMainFragmentHidden = MutableStateFlow(false)
    val isMainFragmentHidden:StateFlow<Boolean> = _isMainFragmentHidden
    fun setMainFragmentStatus(isHidden:Boolean){
        _isMainFragmentHidden.value =isHidden
    }
    // only one observer should receive the updates
   val navigationEvents:Flow<MainFragmentNavigationAction> = mainFragmentNavigationEvents.receiveAsFlow()
    fun openDiscoverScreen(){
        mainFragmentNavigationEvents.tryOffer(MainFragmentNavigationAction.NavigateToDiscoverScreen)
    }
    fun openLatestComicsScreen(){
        mainFragmentNavigationEvents.tryOffer(MainFragmentNavigationAction.NavigateToLatestComicsScreen)
    }
    fun openOngoingComicsScreen(){
        mainFragmentNavigationEvents.tryOffer(MainFragmentNavigationAction.NavigateToOngoingComicsScreen)
    }

}
sealed class MainFragmentNavigationAction{
    object NavigateToDiscoverScreen:MainFragmentNavigationAction()
    object NavigateToLatestComicsScreen:MainFragmentNavigationAction()
    object NavigateToOngoingComicsScreen:MainFragmentNavigationAction()
}

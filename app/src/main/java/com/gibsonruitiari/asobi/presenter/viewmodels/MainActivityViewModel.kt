package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.gibsonruitiari.asobi.common.ScreenSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivityViewModel:ViewModel() {
    private val _screenWidthState = MutableStateFlow(ScreenSize.COMPACT) // default let it be compact
    // to observed
    val screenWidthState:StateFlow<ScreenSize> = _screenWidthState
    fun setScreenWidth(screenSize: ScreenSize){
        _screenWidthState.value = screenSize
    }
}
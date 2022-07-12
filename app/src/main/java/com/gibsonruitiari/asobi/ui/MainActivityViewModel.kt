package com.gibsonruitiari.asobi.ui

import androidx.lifecycle.ViewModel
import com.gibsonruitiari.asobi.utilities.ScreenSize
import com.gibsonruitiari.asobi.ui.uiModels.UiMeasureSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivityViewModel:ViewModel() {
    private val _screenWidthState = MutableStateFlow(ScreenSize.COMPACT) // default let it be compact
    private val _uiMeasureSpecState = MutableStateFlow(UiMeasureSpec.default)
    // to observed
    val screenWidthState:StateFlow<ScreenSize> = _screenWidthState
    val uiMeasureSpecState:StateFlow<UiMeasureSpec> = _uiMeasureSpecState
    fun setUiMeasureSpec(uiMeasureSpec: UiMeasureSpec){
        _uiMeasureSpecState.value = uiMeasureSpec
    }
    fun setScreenWidth(screenSize: ScreenSize){
        _screenWidthState.value = screenSize
    }
}
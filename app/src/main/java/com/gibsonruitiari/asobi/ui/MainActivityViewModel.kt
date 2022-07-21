package com.gibsonruitiari.asobi.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivityViewModel:ViewModel() {
    private val _isInComicsByGenreFragment = MutableStateFlow(false)

    val isInComicsByGenreFragment:StateFlow<Boolean> = _isInComicsByGenreFragment

    fun updateIsInComicsByGenreFragmentState(value:Boolean){
        _isInComicsByGenreFragment.value = value
    }
}
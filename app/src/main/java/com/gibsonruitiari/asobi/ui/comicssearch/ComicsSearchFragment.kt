package com.gibsonruitiari.asobi.ui.comicssearch


import com.gibsonruitiari.asobi.ui.MainNavigationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsSearchFragment:MainNavigationFragment() {
    private val comicsSearchViewModel:ComicsSearchViewModel by viewModel()
}
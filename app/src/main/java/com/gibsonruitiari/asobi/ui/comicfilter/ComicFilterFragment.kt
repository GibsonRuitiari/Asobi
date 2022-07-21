package com.gibsonruitiari.asobi.ui.comicfilter

import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicFilterFragment:ComicsFilterBottomSheet(){
    private val comicsByGenreViewModel: ComicsByGenreViewModel by viewModel(owner = { requireParentFragment() })

    override fun resolveViewModelDelegate(): ComicFilterViewModel =comicsByGenreViewModel


}
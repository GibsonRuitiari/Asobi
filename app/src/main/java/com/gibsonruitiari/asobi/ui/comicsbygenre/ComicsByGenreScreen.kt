package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.graphics.Color
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.ui.uiModels.toUiGenreModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsByGenreScreen: PaginatedFragment() {
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel(owner = {requireParentFragment()})
    override fun onComicClicked(comicItem: ViewComics) {
       Toast.makeText(requireContext(),"comic item: ${comicItem.comicLink}", Toast.LENGTH_SHORT).show()
    }

    override suspend fun asynchronouslyInitializeFragmentViews() {
        fragmentToolbar.title=getString(R.string.comics_by_genre)
        fragmentToolbar.setTitleTextColor(Color.WHITE)
        fragmentToolbar.isTitleCentered=true
        fragmentToolbar.setTitleTextAppearance(requireContext(),R.style.TextAppearance_Asobi_Headline4)
    }
    override fun getFragmentColor(): Int {
        return comicsByGenreViewModel.currentGenreChoice.value?.toUiGenreModel()?.genreColor
            ?: Color.parseColor("#CF0B82")
    }
    override suspend fun observePagedData() {
        comicsByGenreViewModel.comicsList.collectLatest {
            listAdapter.submitData(it)
        }
    }
}
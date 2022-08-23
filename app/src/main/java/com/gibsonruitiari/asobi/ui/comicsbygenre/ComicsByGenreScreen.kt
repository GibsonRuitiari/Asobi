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
    override fun getTitle(): String = requireContext().getString(R.string.comics_by_genre)
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
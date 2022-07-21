package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.MainFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class ComicsByGenreFragment: MainFragment() {
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel()
    override fun onComicClicked(comicItem: ViewComics) {
       Toast.makeText(requireContext(),"comic item: ${comicItem.comicLink}", Toast.LENGTH_SHORT).show()
    }
    override val toolbarTitle: String
        get() = getString(R.string.comics_by_genre)

    override suspend fun observePagedData() {
        comicsByGenreViewModel.comicsList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}
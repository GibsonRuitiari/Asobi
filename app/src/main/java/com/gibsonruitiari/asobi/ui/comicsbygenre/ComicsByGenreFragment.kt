package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsByGenreFragment: PaginatedFragment() {
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel()
    override fun onComicClicked(comicItem: ViewComics) {
       Toast.makeText(requireContext(),"comic item: ${comicItem.comicLink}", Toast.LENGTH_SHORT).show()
    }
    override val toolbarTitle: String
        get() = getString(R.string.comics_by_genre)
    override val fragmentColor: Int
        get() = Color.parseColor("#CF0B82")
    override val fragmentGradient: Drawable
        get() = resources.getDrawable(R.drawable.comics_by_genre_screen_gradient,null)
    override suspend fun observePagedData() {
        comicsByGenreViewModel.comicsList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}
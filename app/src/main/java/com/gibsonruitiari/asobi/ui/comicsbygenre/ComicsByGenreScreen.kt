package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.ui.uiModels.toUiGenreModel
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.interfaces.RSAKey

class ComicsByGenreScreen: PaginatedFragment() {
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel(owner = {requireParentFragment()})
    private val logger:Logger by inject()
    override fun onComicClicked(comicItem: ViewComics) {
       Toast.makeText(requireContext(),"comic item: ${comicItem.comicLink}", Toast.LENGTH_SHORT).show()
    }

    override suspend fun asynchronouslyInitializeFragmentViews() {
        comicsByGenreViewModel.currentGenreChoice.collect {
            it?.let {
                    genres ->
                val g=genres.toUiGenreModel()
                // for some reason fragmentToolbar.title does  not update correctly
                fragmentToolbar.title=getString(R.string.comics_by_genre)
                backgroundImg.background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(g.genreColor,Color.TRANSPARENT))

                logger.i("name--> $g")
            }
        }
    }

    override fun getFragmentColor(): Int {
        return comicsByGenreViewModel.currentGenreChoice.value?.toUiGenreModel()?.genreColor
            ?: Color.parseColor("#CF0B82")
    }
    override suspend fun observePagedData() {
        comicsByGenreViewModel.comicsList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}
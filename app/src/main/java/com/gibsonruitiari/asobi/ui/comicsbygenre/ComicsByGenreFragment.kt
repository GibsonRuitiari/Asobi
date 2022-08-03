package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsByGenreFragment: PaginatedFragment() {
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel(owner = {requireParentFragment()})
    private var color:Int=  Color.parseColor("#CF0B82")
    override fun onComicClicked(comicItem: ViewComics) {
       Toast.makeText(requireContext(),"comic item: ${comicItem.comicLink}", Toast.LENGTH_SHORT).show()
    }

    override val toolbarTitle: String
        get() = getString(R.string.comics_by_genre)
    override val fragmentColor: Int
        get() = color
    override val fragmentGradient: Drawable
        get() = resources.getDrawable(R.drawable.comics_by_genre_screen_gradient,null)
    override suspend fun observePagedData() {
      // comicsByGenreViewModel.loadData()

        comicsByGenreViewModel.comicsList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}
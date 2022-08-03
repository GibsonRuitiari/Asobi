package com.gibsonruitiari.asobi.ui.latestcomics

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class LatestComicsFragment: PaginatedFragment() {
    override
    fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val latestComicsViewModel: LatestComicsViewModel by viewModel()

    override suspend fun asynchronouslyInitializeFragmentViews() {
        fragmentToolbar.title = getString(R.string.latest_comics)
        backgroundImg.background=resources.getDrawable(R.drawable.latest_screen_gradient,null)
    }
    override fun getFragmentColor(): Int = Color.parseColor("#FF7403")
    override suspend fun observePagedData() {
        latestComicsViewModel.pagedList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }

}

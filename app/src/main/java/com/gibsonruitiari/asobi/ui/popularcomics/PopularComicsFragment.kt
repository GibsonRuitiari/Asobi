package com.gibsonruitiari.asobi.ui.popularcomics

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class PopularComicsFragment: PaginatedFragment() {
     override fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val popularComicsViewModel: PopularComicsViewModel by viewModel()

    override fun getFragmentColor(): Int =  Color.parseColor("#FFA402")

    override suspend fun asynchronouslyInitializeFragmentViews() {
        fragmentToolbar.title=getString(R.string.popular_comics)
        backgroundImg.background=resources.getDrawable(R.drawable.popular_comics_screen_gradient,null)
    }

    override suspend fun observePagedData() {
       popularComicsViewModel.pagedList.collectLatest {
           pagingListAdapter?.submitData(it)
       }
    }

}
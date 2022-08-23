package com.gibsonruitiari.asobi.ui.popularcomics

import android.graphics.Color
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

    override fun getTitle(): String=requireContext().getString(R.string.popular_comics)

    override suspend fun observePagedData() {
       popularComicsViewModel.pagedList.collectLatest {
           listAdapter.submitData(it)
       }
    }

}
package com.gibsonruitiari.asobi.ui.popularcomics

import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.MainFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class PopularComicsFragment: MainFragment() {
     override fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val popularComicsViewModel: PopularComicsViewModel by viewModel()
    override val toolbarTitle: String
        get() = getString(R.string.popular_comics)

    override suspend fun observePagedData() {
       popularComicsViewModel.pagedList.collectLatest {
           pagingListAdapter?.submitData(it)
       }
    }

}
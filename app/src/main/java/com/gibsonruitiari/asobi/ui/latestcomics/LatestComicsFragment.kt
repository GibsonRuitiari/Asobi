package com.gibsonruitiari.asobi.ui.latestcomics

import android.graphics.Color
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
    override val toolbarTitle: String
        get() = getString(R.string.latest_comics)
    override val fragmentColor: Int
        get() = Color.parseColor("#FF7403")
    override suspend fun observePagedData() {
        latestComicsViewModel.pagedList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }

}

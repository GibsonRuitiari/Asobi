package com.gibsonruitiari.asobi.ui.ongoingcomics

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class OngoingComicsFragment : PaginatedFragment() {
    override
    fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val ongoingComicsViewModel: OngoingComicsViewModel by viewModel()
    override val toolbarTitle: String
        get() = getString(R.string.ongoing_comics)
    override val fragmentGradient: Drawable
        get() = resources.getDrawable(R.drawable.ongoing_comics_screen_gradient,null)
    override val fragmentColor: Int
        get() = Color.parseColor("#D46C4E")
    override suspend fun observePagedData() {
        ongoingComicsViewModel.pagedList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}
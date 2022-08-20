package com.gibsonruitiari.asobi.ui.completedcomics

import android.graphics.Color
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedComicsFragment: PaginatedFragment() {
    override fun onComicClicked(comicItem: ViewComics) {
        Toast.makeText(requireContext(), "comic ${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val completedComicsViewModel: CompletedComicsViewModel by viewModel()
    override suspend fun observePagedData() {
       completedComicsViewModel.pagedList.collectLatest {
           listAdapter.submitData(it)
       }
    }
    override suspend fun asynchronouslyInitializeFragmentViews() {
        fragmentToolbar.title=getString(R.string.completed_comics)
        fragmentToolbar.setTitleTextColor(Color.WHITE)
        fragmentToolbar.isTitleCentered=true
        fragmentToolbar.setTitleTextAppearance(requireContext(),R.style.TextAppearance_Asobi_Headline4)    }

    override fun getFragmentColor(): Int = Color.parseColor("#4cc9f0")


}
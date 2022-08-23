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

    override fun getTitle(): String =requireContext().getString(R.string.completed_comics)

    override fun getFragmentColor(): Int = Color.parseColor("#4cc9f0")


}
package com.gibsonruitiari.asobi.ui.ongoingcomics

import android.graphics.Color
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class OngoingComicsFragment : PaginatedFragment() {
    override
    fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicName} clicked", Toast.LENGTH_SHORT).show()
    }
    private val ongoingComicsViewModel: OngoingComicsViewModel by viewModel()
    override fun getFragmentColor(): Int =  Color.parseColor("#D46C4E")
    override fun getTitle(): String = requireContext().getString(R.string.ongoing_comics)
    override suspend fun observePagedData() {
        ongoingComicsViewModel.pagedList.collectLatest {
            listAdapter.submitData(it)
        }
    }
}
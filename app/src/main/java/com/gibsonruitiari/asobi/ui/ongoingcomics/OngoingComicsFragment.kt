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
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val ongoingComicsViewModel: OngoingComicsViewModel by viewModel()


    override suspend fun asynchronouslyInitializeFragmentViews() {
        fragmentToolbar.title=getString(R.string.ongoing_comics)
        fragmentToolbar.setTitleTextColor(Color.WHITE)
        fragmentToolbar.isTitleCentered=true
        fragmentToolbar.setTitleTextAppearance(requireContext(),R.style.TextAppearance_Asobi_Headline4)    }

    override fun getFragmentColor(): Int =  Color.parseColor("#D46C4E")

    override suspend fun observePagedData() {
        ongoingComicsViewModel.pagedList.collectLatest {
            listAdapter.submitData(it)
        }
    }
}
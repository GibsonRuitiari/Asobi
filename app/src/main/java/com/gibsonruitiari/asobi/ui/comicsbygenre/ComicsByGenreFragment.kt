package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.MainFragment
import com.gibsonruitiari.asobi.ui.comicfilter.ComicFilterFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class ComicsByGenreFragment: MainFragment() {
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel()
    private val backPressHandlerCallback = object: OnBackPressedCallback(false){
        override fun handleOnBackPressed() {
           filterFabButton.visibility = View.GONE
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(backPressHandlerCallback)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filterFabButton.visibility = View.VISIBLE
        val comicFilterSheet= childFragmentManager.findFragmentById(R.id.filter_sheet) as ComicFilterFragment
        filterFabButton.setOnClickListener {
            comicFilterSheet.showFiltersSheet()
        }
    }
    override fun onComicClicked(comicItem: ViewComics) {
       Toast.makeText(requireContext(),"comic item: ${comicItem.comicLink}", Toast.LENGTH_SHORT).show()
    }
    override val toolbarTitle: String
        get() = getString(R.string.comics_by_genre)

    override suspend fun observePagedData() {
        comicsByGenreViewModel.comicsList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}
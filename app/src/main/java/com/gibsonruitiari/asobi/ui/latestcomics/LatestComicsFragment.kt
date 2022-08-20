package com.gibsonruitiari.asobi.ui.latestcomics



import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.PaginatedFragment
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LatestComicsFragment: PaginatedFragment() {
    private val logger:Logger by inject()
    override
    fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.appbar.setOnScrollChangeListener { v, _, scrollY, _, _ ->
           doActionIfWeAreOnDebug {  logger.i("app bar scrolling $scrollY") }
        }
    }
    private val latestComicsViewModel: LatestComicsViewModel by viewModel()
    override suspend fun asynchronouslyInitializeFragmentViews() {
        fragmentToolbar.title = getString(R.string.latest_comics)
        fragmentToolbar.setTitleTextColor(Color.WHITE)
        fragmentToolbar.isTitleCentered=true
        fragmentToolbar.setTitleTextAppearance(requireContext(),R.style.TextAppearance_Asobi_Headline4)
    }
    override fun getFragmentColor(): Int = Color.parseColor("#74B7A7")
    override suspend fun observePagedData() {
        latestComicsViewModel.pagedList.collectLatest {
            listAdapter.submitData(it)
        }
    }
}

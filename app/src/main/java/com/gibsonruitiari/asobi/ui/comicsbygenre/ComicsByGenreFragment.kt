package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.MainFragment
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.composedPagedAdapter
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.extensions.loadPhotoUrl
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsByGenreFragment:MainFragment<ViewComics>() {

    private val mainActivityViewModel:MainActivityViewModel by viewModel()
    private var BindingViewHolder<ComicItemLayoutBinding>.item by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(viewComics: ViewComics?){
        viewComics?.let {comic->
            this.item = comic
            with(binding){
                comicsImageView.loadPhotoUrl(comic.comicThumbnail)
            }
        }
    }
    private val onBackPressedCallback = object :OnBackPressedCallback(false){
        override fun handleOnBackPressed() {
            /* set the flag to false to ensure the filter button is hidden */
            mainActivityViewModel.updateIsInComicsByGenreFragmentState(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel.updateIsInComicsByGenreFragmentState(true)
    }
    @Suppress("UNCHECKED_CAST")
    override fun createComposedPagedAdapter(): PagingDataAdapter<ViewComics, RecyclerView.ViewHolder> =
        composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
            viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
                itemView.setOnClickListener { onComicClicked(item) }
            }
        }, bindViewHolder = { viewHolder: RecyclerView.ViewHolder, item: ViewComics?, _ ->
            (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
        })
    private fun onComicClicked(comicItem: ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val comicsByGenreViewModel: ComicsByGenreViewModel by viewModel()
    override val toolbarTitle: String
        get() = getString(R.string.comics_by_genre)
    override suspend fun observePagedData() {
        comicsByGenreViewModel.comicsList.collectLatest {
            pagingListAdapter?.submitData(it)
        }

    }
}
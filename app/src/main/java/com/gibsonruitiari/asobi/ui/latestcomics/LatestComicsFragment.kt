package com.gibsonruitiari.asobi.ui.latestcomics

import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.loadPhotoUrl
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.ui.MainFragment
import com.gibsonruitiari.asobi.ui.comicsadapters.*
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class LatestComicsFragment: MainFragment<ViewComics>() {
    private var BindingViewHolder<ComicItemLayoutBinding>.item by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(viewComics: ViewComics?){
        viewComics?.let {comic->
            this.item = comic
            with(binding){
                comicsImageView.loadPhotoUrl(comic.comicThumbnail)
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    override fun createComposedPagedAdapter(): PagingDataAdapter<ViewComics, RecyclerView.ViewHolder> =
        composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
            viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
                itemView.setOnClickListener { onComicClicked(item) }
            }
        }, bindViewHolder = { viewHolder: RecyclerView.ViewHolder, item:ViewComics?, _ ->
            (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
        })
    private fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private val latestComicsViewModel: LatestComicsViewModel by viewModel()
    override val toolbarTitle: String
        get() = getString(R.string.latest_comics)
    override suspend fun observePagedData() {
        latestComicsViewModel.pagedList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
}

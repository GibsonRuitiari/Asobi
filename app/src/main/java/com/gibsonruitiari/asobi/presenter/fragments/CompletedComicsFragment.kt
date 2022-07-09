package com.gibsonruitiari.asobi.presenter.fragments

import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.common.extensions.loadPhotoUrl
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.presenter.base.BaseFragment
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.composedPagedAdapter
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.BindingViewHolder
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.viewHolderDelegate
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.viewHolderFrom
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import com.gibsonruitiari.asobi.presenter.viewmodels.CompletedComicsViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedComicsFragment:BaseFragment<ViewComics>() {
    private var BindingViewHolder<ComicItemLayoutBinding>.item by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(viewComics: ViewComics?){
        viewComics?.let {comic->
            this.item = comic
            with(binding){
                comicsImageView.loadPhotoUrl(comic.comicThumbnail)
            }
        }
    }
    private fun onComicClicked(comicItem:ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    @Suppress("UNCHECKED_CAST")
    override fun createComposedPagedAdapter(): PagingDataAdapter<ViewComics, RecyclerView.ViewHolder> =
        composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
            viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
                itemView.setOnClickListener { onComicClicked(item) }
            }
        }, bindViewHolder = {viewHolder:RecyclerView.ViewHolder, item:ViewComics?, _ ->
            (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
        })
    private val completedComicsViewModel:CompletedComicsViewModel by viewModel()
    override suspend fun observePagedData() {
       completedComicsViewModel.pagedList.collectLatest {
           pagingListAdapter?.submitData(it)
       }
    }
    override val toolbarTitle: String
        get() = getString(R.string.completed_comics)

}
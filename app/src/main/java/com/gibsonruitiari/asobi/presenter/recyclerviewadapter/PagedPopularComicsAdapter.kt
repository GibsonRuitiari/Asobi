package com.gibsonruitiari.asobi.presenter.recyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.common.extensions.loadPhotoUrl
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff.DiffItemAdapterCallback
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics

class PagedPopularComicsAdapter(private val onComicClicked:(ViewComics)->Unit):PagingDataAdapter<ViewComics,PagedPopularComicsAdapter
.PopularComicsViewHolder>(DiffItemAdapterCallback()) {

    inner class PopularComicsViewHolder(private val comicItemLayoutBinding: ComicItemLayoutBinding):RecyclerView.ViewHolder(comicItemLayoutBinding.root){
          fun bind(comics: ViewComics?){
              comics?.let { comic->
                  comicItemLayoutBinding.comicsImageView.loadPhotoUrl(comic.comicThumbnail)
                  comicItemLayoutBinding.comicsImageView.setOnClickListener { onComicClicked(comic) }
              }
          }
    }
    override fun onBindViewHolder(holder: PopularComicsViewHolder, position: Int) {
       val comic= getItem(position)
        holder.bind(comic)
    }
    object ViewComicsDiffCallback : DiffUtil.ItemCallback<ViewComics>() {
        override fun areItemsTheSame(oldItem: ViewComics, newItem: ViewComics): Boolean {
            // Id is unique.
            return oldItem.comicName == newItem.comicName && oldItem.comicLink == newItem.comicLink
        }
        override fun areContentsTheSame(oldItem: ViewComics, newItem: ViewComics): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularComicsViewHolder {
        val binding=ComicItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PopularComicsViewHolder(binding)
    }
}
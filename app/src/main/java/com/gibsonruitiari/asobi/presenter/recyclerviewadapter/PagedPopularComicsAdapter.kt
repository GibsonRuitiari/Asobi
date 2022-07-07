package com.gibsonruitiari.asobi.presenter.recyclerviewadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.common.utils.loadPhotoUrl
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff.DiffItemAdapterCallback
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics

class PagedPopularComicsAdapter(private val callback:ItemEventCallback):PagingDataAdapter<ViewComics,PagedPopularComicsAdapter.PopularComicsViewHolder>(DiffItemAdapterCallback<ViewComics>()) {
    inner class PopularComicsViewHolder(private val comicItemLayoutBinding: ComicItemLayoutBinding):RecyclerView.ViewHolder(comicItemLayoutBinding.root){
          fun bind(comics: ViewComics,
          callback:ItemEventCallback){
              with(comicItemLayoutBinding){
                  comicsImageView.loadPhotoUrl(comics.comicThumbnail)
                  with(comicsImageView){
                      setOnClickListener {
                          callback.onComicClicked(comics)
                      }
                      setOnLongClickListener {
                          callback.onComicLongClicked(comics)
                          true
                      }
                  }

              }
          }
    }
    interface ItemEventCallback{
        fun onComicClicked(comics: ViewComics)
        fun onComicLongClicked(comics: ViewComics)
    }
    override fun onBindViewHolder(holder: PopularComicsViewHolder, position: Int) {
        with(getItem(position)){
            this?.let {
                holder.bind(it,callback)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularComicsViewHolder {
        val binding=ComicItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PopularComicsViewHolder(binding)
    }
}
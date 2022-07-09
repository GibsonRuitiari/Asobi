package com.gibsonruitiari.asobi.presenter.recyclerviewadapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff.DiffItemAdapterCallback

private class AsobiPagerAdapter<ItemT:Any,VH:RecyclerView.ViewHolder>(
    private val viewHolderCreator:(ViewGroup,Int)->VH,
    private val viewHolderBinder:(holder:VH,item:ItemT?,position:Int)->Unit):PagingDataAdapter<ItemT,VH>(DiffItemAdapterCallback()) {
    override fun onBindViewHolder(holder: VH, position: Int) {
        viewHolderBinder(holder,getItem(position),position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
       return viewHolderCreator(parent,viewType)
    }
}
 fun<Items:Any,VH:RecyclerView.ViewHolder>composedPagedAdapter(createViewHolder:(ViewGroup,
Int)->VH,bindViewHolder:(viewHolder:VH,item:Items?,itemPosition:Int)->Unit):PagingDataAdapter<Items,VH>{
   return AsobiPagerAdapter(createViewHolder,bindViewHolder)
}


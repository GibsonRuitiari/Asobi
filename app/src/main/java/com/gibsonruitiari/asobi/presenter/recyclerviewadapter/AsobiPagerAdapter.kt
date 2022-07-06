package com.gibsonruitiari.asobi.presenter.recyclerviewadapter

import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff.DiffItemAdapterCallback

private class AsobiPagerAdapter<ItemT:Any,VH:RecyclerView.ViewHolder>(
    private val viewHolderCreator:(ViewGroup,Int)->VH,
    // view holder, item and position
    private val viewHolderBinder:(holder:VH,position:Int)->Unit):PagingDataAdapter<ItemT,VH>(DiffItemAdapterCallback()) {
    override fun onBindViewHolder(holder: VH, position: Int) {
        viewHolderBinder(holder,position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
       return viewHolderCreator(parent,viewType)
    }

}
// ViewLifecycleOwner.lifecycleScope.launch{to collect pagedItems}
suspend fun <ItemT:Any,VH:RecyclerView.ViewHolder> pagedAdapterOf(pagedItems:PagingData<ItemT>,
                                          // invalidate paging source use this on onSwipeRefresh action
                                                                  onRefresh:(()->Unit)?=null,
                                                                  // retry to fetch data without invalidating the paging source in case of a LoadState.Error
                                                                  onRetry:(()->Unit)?=null,
                                                          viewHolderCreator: (parent: ViewGroup, viewType: Int) -> VH,
                                                          viewHolderBinder: (holder: VH, position: Int) -> Unit):PagingDataAdapter<ItemT,VH> = AsobiPagerAdapter<ItemT, VH>(viewHolderCreator,
viewHolderBinder = viewHolderBinder).apply {
    onRetry?.let {
        retry()
    }
    onRefresh?.let {
        refresh()
    }
    submitData(pagedItems)
}

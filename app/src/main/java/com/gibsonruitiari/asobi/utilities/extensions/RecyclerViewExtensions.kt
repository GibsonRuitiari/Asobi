package com.gibsonruitiari.asobi.utilities.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Returns a [LinearLayoutManager] in the [RecyclerView.VERTICAL] orientation
 */
fun RecyclerView.verticalLayoutManager(reverseLayout: Boolean = false) =
    LinearLayoutManager(context, RecyclerView.VERTICAL, reverseLayout)

/**
 * Returns a [LinearLayoutManager] in the [RecyclerView.HORIZONTAL] orientation
 */
fun RecyclerView.horizontalLayoutManager(reverseLayout: Boolean = false) =
    LinearLayoutManager(context, RecyclerView.HORIZONTAL, reverseLayout)

/**
 * Returns a [GridLayoutManager] with a span count of [spanCount], and an optional span size lookup
 */
fun RecyclerView.gridLayoutManager(
    spanCount: Int = 1,
    spanSizeLookup: ((position: Int) -> Int)? = null
): GridLayoutManager = GridLayoutManager(context, spanCount).apply {
    orientation = GridLayoutManager.VERTICAL
    if (spanSizeLookup != null) setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = spanSizeLookup.invoke(position)
    })
}

/**
 * Convenience method that takes an instance of a recycler view and sets it up
 * set up applies to recycler views using grid/horizontal layout manager
 */
fun<VH:RecyclerView.ViewHolder> RecyclerView.defaultRecyclerViewSetUp(scrollToTop:Boolean=true,
                                                                      paddingBottom:Int=0,
                                                                      paddingTop:Int=0,
                                                                      paddingStart:Int=0,
                                                                      paddingEnd:Int=0,
                                                                      itemDecoration: ItemDecoration?=null,
                                                                      gridLayout:Boolean=false,
                                                                      hasFixedSize:Boolean=true,
                                                                      recyclerViewAdapter:RecyclerView.Adapter<VH>){
    applyStartWindowInsets(paddingStart)
    applyEndWindowInsets(paddingEnd)
    applyTopWindowInsets(paddingTop)
    applyBottomWindowInsets(paddingBottom=paddingBottom)
    if (hasFixedSize) setHasFixedSize(true)
    if (scrollToTop) scrollToTop()
    adapter = recyclerViewAdapter

    layoutManager = if (gridLayout){
        /*By default the medium density is 160f so we minus 4 just increase to accommodate smaller screens and come up with a proper
        * no of span count for our grid layout */
        gridLayoutManager(spanCount = (screenWidth/156f).toInt())
    }else{
        horizontalLayoutManager()
    }
    itemDecoration?.let { addItemDecoration(it) }
}

/**
 * Convenience method that allows one to easily add onScrollListener for
 * recycler view
 *
 */
inline fun RecyclerView.onScrollListener(noinline onScrollStateChange:((recyclerViewInstance:RecyclerView,
scrollState:Int)->Unit)?=null,
crossinline onScroll:((recyclerViewInstance:RecyclerView,xPixelsConsumed:Int,yPixelsConsumed:Int)->Unit)){
    addOnScrollListener(object :RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            onScrollStateChange?.invoke(recyclerView,newState)
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScroll.invoke(recyclerView,dx,dy)

        }
    })
}

fun RecyclerView.scrollToTop() {
    layoutManager?.let {
        val firstVisibleItemPosition = it.findFirstVisibleItemPosition()
        if (firstVisibleItemPosition > 6) {
            scrollToPosition(6)
        }
        smoothScrollToPosition(0)

        if (it is StaggeredGridLayoutManager) {
            it.invalidateSpanAssignments()
        }
    }
}
fun RecyclerView.LayoutManager?.findFirstVisibleItemPosition(): Int {
    return when (this) {
        is LinearLayoutManager -> findFirstVisibleItemPosition()
        is GridLayoutManager -> findFirstVisibleItemPosition()
        is StaggeredGridLayoutManager -> findFirstVisibleItemPositions(null).first()
        else -> RecyclerView.NO_POSITION
    }
}

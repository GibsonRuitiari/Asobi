package com.gibsonruitiari.asobi.common.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    if (spanSizeLookup != null) setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = spanSizeLookup.invoke(position)
    })
}

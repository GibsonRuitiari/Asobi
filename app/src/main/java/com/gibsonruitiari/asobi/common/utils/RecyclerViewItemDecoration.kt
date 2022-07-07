package com.gibsonruitiari.asobi.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * setting margin/padding to recycler view items does not work to grid items
 * thus we need to extend [RecyclerView.ItemDecoration] to ensure each column
 * has same width and gutter space
 * @param spanCount the number of columns to be applied to the grid recycler view
 * @param spacing gutter space to apply
 * @param includeEdge whether apply spacing to the edges also
 */
class RecyclerViewItemDecoration (private val spanCount:Int,
                                  private val spacing:Int,
                                  private val includeEdge:Boolean):RecyclerView.ItemDecoration (){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position %spanCount
        if (includeEdge){
            outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom=spacing // item bottom
        }else{
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}
fun Activity.convertToPxFromDp(valueInDp:Int) = (valueInDp * resources.displayMetrics.density).roundToInt()

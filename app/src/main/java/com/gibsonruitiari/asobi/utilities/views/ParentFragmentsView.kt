package com.gibsonruitiari.asobi.utilities.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout

/**This will act as the parent/base view-group for most of the fragments that need to show states:loading,error,data
 *  used across the application
 * this class is extensible hence fragments can extend this class to construct their views appropriately
 * This view-group extends a coordinator layout to enable easier addition of behaviors of children
 */
open class ParentFragmentsView  (context: Context):CoordinatorLayout(context){
   val loadingStateLayout:LoadingLayout
   val errorEmptyStateLayout:ConstraintLayout
    init {
        id = ViewCompat.generateViewId()
        fitsSystemWindows=true
        background = context.resources.getDrawable(R.color.matte,null)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
        loadingStateLayout = loadingLayout(context)
        errorEmptyStateLayout = ErrorStateLayout(context).apply { visibility=View.GONE }
        this.addView(loadingStateLayout)
        this.addView(errorEmptyStateLayout)
    }
    private fun loadingLayout(context: Context):LoadingLayout =
        LoadingLayout(context).apply {
            id=ViewCompat.generateViewId()
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
           visibility = View.GONE
        }

}
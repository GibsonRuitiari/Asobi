package com.gibsonruitiari.asobi.utilities

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout



class StatusBarScrimBehavior @JvmOverloads constructor(context:Context,attr:AttributeSet?=null)
    :CoordinatorLayout.Behavior<View>(context,
attr) {
    /* since the coordinator layout has set fitSystemWindows:true, then we need to return the same insets
    * instead of creating our own inset policy; the insets will be applied as padding, by this view  */
    private val noopWindowInsetsListener = View.OnApplyWindowInsetsListener { _, insets -> insets }
    /* Before this view is laid out by the coordinator layout, apply window insets listener to it then
    * return false to ensure the default method is used to lay out this view rather than our own implementation */
    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        child.setOnApplyWindowInsetsListener(noopWindowInsetsListener)
        return false
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout){
            /*If the app bar layout has animatable drawable (drawables that have state eg color state
            * jump the in between animation, and set the current state to the end value   */
            dependency.jumpDrawablesToCurrentState()
            child.elevation = dependency.elevation
            return true
        }
        return false
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.elevation = dependency.elevation
        /* does not change the position/size of the status bar scrim thus return false  */
        return false
    }
    // whenever parents insets change, allow this behavior to handle the inset change of this view
    override fun onApplyWindowInsets(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        insets: WindowInsetsCompat
    ): WindowInsetsCompat {
        //WindowInsetsCompat.Type.navigationBars()
        child.layoutParams.height=  insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        // insets have change request parent to layout the child again on the next frame
        child.requestLayout()
        return insets
    }

}
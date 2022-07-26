package com.gibsonruitiari.asobi.ui

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import com.gibsonruitiari.asobi.R


class MainFragmentView constructor(context:Context): ConstraintLayout(context){
    var fragmentContainerView: FragmentContainerView
    init {
        id = ViewCompat.generateViewId()
        layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        val matterColorBackground=resources.getColor(R.color.matte, null)
        setBackgroundColor(matterColorBackground)
         fragmentContainerView = constructFragmentContainerView()
        addView(fragmentContainerView)
    }
    private fun constructFragmentContainerView(): FragmentContainerView {
        val fragmentContainerView = FragmentContainerView(context).apply {
            id = ViewCompat.generateViewId()
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.constrainHeight(fragmentContainerView.id,
            ConstraintSet.MATCH_CONSTRAINT_SPREAD)
        constraintSet.constrainWidth(fragmentContainerView.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
        constraintSet.connect(fragmentContainerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID,
            ConstraintSet.START)
        constraintSet.connect(fragmentContainerView.id, ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START)
        constraintSet.connect(fragmentContainerView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(fragmentContainerView.id, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM)
        constraintSet.applyTo(this)
        return  fragmentContainerView
    }
}

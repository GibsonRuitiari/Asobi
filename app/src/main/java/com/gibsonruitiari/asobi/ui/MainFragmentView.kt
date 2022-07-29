package com.gibsonruitiari.asobi.ui

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import com.gibsonruitiari.asobi.R


class MainFragmentView constructor(context:Context): ConstraintLayout(context){
    val containerView:FragmentContainerView
    init {
        id = ViewCompat.generateViewId()
        layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        val matterColorBackground=resources.getColor(R.color.matte, null)
        setBackgroundColor(matterColorBackground)
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        containerView=setUpFrameLayoutContainer(this.context, constraintSet)
        constraintSet.applyTo(this)
    }
    private fun setUpFrameLayoutContainer(context: Context,constraintSet: ConstraintSet):FragmentContainerView{
        val container=FragmentContainerView(context).apply {
            id = ViewCompat.generateViewId()
            val matterColorBackground=resources.getColor(R.color.matte, null)
            setBackgroundColor(matterColorBackground)
        }
        addView(container)
        constraintSet.constrainHeight(container.id,ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainWidth(container.id, ConstraintSet.MATCH_CONSTRAINT)

        constraintSet.connect(container.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(container.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(container.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(container.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        return container
    }

}

package com.gibsonruitiari.asobi.utilities.views

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.*

/* A container view is an extensible class, that is meant to be extended by fragments that are supposed to house other
* fragments essentially, it acts as a fragment container view */
class ContainerView constructor(context:Context):ConstraintLayout(context) {
    val fragmentContainerView:FragmentContainerView
    // might to leak?
    private val containerViewInstance by lazy { this }

    init {
        id = ViewCompat.generateViewId()
        layoutParams=  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
        containerViewInstance.setBackgroundColor(resources.getColor(R.color.matte,null))
        val constraintSet = ConstraintSet()
        fragmentContainerView =fragmentContainerViewSetUp(context, constraintSet)
        constraintSet.applyTo(containerViewInstance)
    }
    private fun fragmentContainerViewSetUp(context: Context,constraintSet: ConstraintSet):FragmentContainerView{
        val container=FragmentContainerView(context).apply {
            id = ViewCompat.generateViewId()
            val matterColorBackground=resources.getColor(R.color.matte, null)
            setBackgroundColor(matterColorBackground)
        }
        addView(container)
        constraintSet.setViewLayoutParams(container.id, ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.MATCH_CONSTRAINT)
        constraintSet constrainBottomToParent container.id
        constraintSet constrainTopToParent container.id
        constraintSet constrainEndToParent container.id
        constraintSet constrainStartToParent container.id
        return container
    }
}
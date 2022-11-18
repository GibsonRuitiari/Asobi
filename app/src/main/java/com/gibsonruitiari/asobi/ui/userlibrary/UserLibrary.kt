package com.gibsonruitiari.asobi.ui.userlibrary

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.noElevation
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class UserLibrary : Fragment() {
    private val logger:Logger by inject()
    private var job:Job?=null
    /* Start: View variables initialization  */
    private lateinit var rootView:CoordinatorLayout
    /* End: View variables initialization  */
    inner class ComicDetailsScreen(context:Context):ParentFragmentsView(context){
        private val constraintSet:ConstraintSet = ConstraintSet()
        private val onScrollColor = context.resources.getColor(R.color.davy_grey,null)
        private val onFinishedScrollColor = context.resources.getColor(R.color.matte,null)
        private val transparentColor = context.resources.getColor(R.color.transparent,null)
        private val noElevationAnimator = StateListAnimator()
        private val comicDetailsAppBarLayout:AppBarLayout
        get() {
           return AppBarLayout(context).apply {
                id=ViewCompat.generateViewId()
                background=null
                noElevationAnimator.addState(IntArray(0),ObjectAnimator.ofFloat(this,"elevation", noElevation))
                stateListAnimator= noElevationAnimator
                elevation= noElevation
                setBackgroundColor(transparentColor)
                constraintSet.setViewLayoutParams(this.id,ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.WRAP_CONTENT)
                constraintSet constrainEndToParent this.id
                constraintSet constrainTopToParent  this.id
                constraintSet constrainStartToParent this.id
                this.addView(toolbar)
            }
        }
        private val toolbar:MaterialToolbar
        get() {
            return MaterialToolbar(context).apply {
                id=ViewCompat.generateViewId()
                elevation= noElevation
                noElevationAnimator.addState(IntArray(0),ObjectAnimator.ofFloat(this,"elevation", noElevation))
                stateListAnimator= noElevationAnimator
                isTitleCentered=true
                background=null
                title=context.getString(R.string.terror_town)
                setTitleTextAppearance(context,R.style.TextAppearance_Asobi_Body1)
                setBackgroundColor(onFinishedScrollColor)
                layoutParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT,AppBarLayout.LayoutParams.WRAP_CONTENT)
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags= AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL + AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            }
        }
//        private val comicCover:ShapeableImageView
//        get() {
//           return ShapeableImageView(context).apply {
//                id=ViewCompat.generateViewId()
//                loadPhoto(AppCompatResources.getDrawable(context,R.drawable.sunstone)!!)
//                shapeAppearanceModel= ShapeAppearanceModel().withCornerSize(12f)
//                scaleType=ImageView.ScaleType.CENTER_CROP
//                adjustViewBounds=true
//                constraintSet.setViewLayoutParams(this.id,120.dp,180.dp)
//                constraintSet.applyMargin(this.id, marginTop = 10.dp)
//                constraintSet constrainStartToParent comicCover.id
//                constraintSet.connect(comicCover.id,ConstraintSet.TOP,comicDetailsAppBarLayout.id,ConstraintSet.BOTTOM)
//                constraintSet constrainEndToParent  comicCover.id
//                strokeWidth=0f
//                strokeColor= ColorStateList.valueOf(transparentColor)
//           }
//        }
        private val constraintLayout:ConstraintLayout
        get() {
            return ConstraintLayout(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                this.addView(comicDetailsAppBarLayout)
                constraintSet.applyTo(this)
            }
        }
        private val nestedScrollView:NestedScrollView
        get() {
           return NestedScrollView(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                this.addView(constraintLayout)
                this.setOnScrollChangeListener { v, _, scrollY, _, _ ->
                    if (v.canScrollVertically(-1).not()){
                        logger.i("cannot scroll vertically")
                        changeStatusBarColor(onFinishedScrollColor)
                        changeElevationAndBackgroundColor(onFinishedScrollColor,0f)
                    }else if (scrollY >0 || scrollY<-1){
                        changeStatusBarColor(onScrollColor)
                        changeElevationAndBackgroundColor(onScrollColor,4f)
                    }
                }
            }
        }
        init {
            addView(nestedScrollView)
        }

        private fun changeElevationAndBackgroundColor(color:Int,barsElevation:Float){
            comicDetailsAppBarLayout.apply {
                background=null
                setBackgroundColor(color)
                elevation=barsElevation
            }
            toolbar.apply {
                setBackgroundColor(color)
                background=null
                elevation=barsElevation
            }
        }
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        doLoadData(hidden)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = ComicDetailsScreen(requireContext())
       return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doLoadData(isHidden)

    }
    private fun doLoadData(isHidden:Boolean) {
        if (!isHidden) {
           initializeJobIfShown()
        } else {
            job.cancelIfActive()
        }
    }

    private fun initializeJobIfShown(){
        job?.cancel() // cancel existing job
        job=viewLifecycleOwner.lifecycleScope.launch{
            /* Coroutine will be automatically stopped whenever we approach onStop and started whenever we approach onStart()
            * so this saves us from cancelling and starting the job in onStop and onStart respectively  */
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                logger.i("hello from this coroutine scope")}
            }
    }

}
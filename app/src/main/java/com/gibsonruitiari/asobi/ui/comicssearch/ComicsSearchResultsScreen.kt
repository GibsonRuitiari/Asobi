package com.gibsonruitiari.asobi.ui.comicssearch

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsSearchResultsScreen: Fragment() {
    /* Start: initialization of view variables */
    private lateinit var searchResultsScreenToolbar:MaterialToolbar
    private lateinit var searchResultsScreenSearchTitle:AppCompatTextView
    private lateinit var searchResultsScreenSearchSubtitle:AppCompatTextView
    /* End: initialization of view variables */

    private var toolbarExpanded:Boolean =false
    private val mainActivityViewModel:MainActivityViewModel by viewModel()
    private val logger:Logger by inject()

    /* Start: Fragment view */
    internal class ComicsSearchResultsView (context:Context)
        :ParentFragmentsView(context){
        lateinit var searchExplanationSubtitle:AppCompatTextView
        lateinit var searchExplanationTitle:AppCompatTextView
        lateinit var searchResultsToolbar:MaterialToolbar
        init {
            searchScreenResultsAppBar(context)
            searchScreenResultsConstraintLayout(context)
        }
        private fun searchScreenResultsAppBar(context: Context){
            val appBarLayout  = AppBarLayout(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                elevation= noElevationValue
            }
            addView(appBarLayout)
            searchResultsToolbar = searchScreenResultsMaterialToolbar(context)
            appBarLayout.addView(searchResultsToolbar)
        }
        private fun searchScreenResultsMaterialToolbar(context: Context):MaterialToolbar{
            val materialToolbar = MaterialToolbar(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                background = context.getDrawable(R.drawable.toolbar_bg)
                setTitleTextColor(Color.WHITE)
                isTitleCentered=true
                title = context.getString(R.string.search_label)
                /* elevation not being changed not working for some reason? */
                elevation= noElevationValue
                (layoutParams as AppBarLayout.LayoutParams).setMargins(toolbarStartMargin.dp)
            }
            return materialToolbar
        }
        private fun searchScreenResultsConstraintLayout(context: Context){
            val constraintLayout = ConstraintLayout(context).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
                id=ViewCompat.generateViewId()
                fitsSystemWindows=true
            }
            addView(constraintLayout)
            val constraintSet = ConstraintSet()
            searchExplanationTitle = searchScreenResultsSearchTitle(context,constraintSet)
            constraintLayout.addView(searchExplanationTitle)
            searchExplanationSubtitle = searchScreenResultsSearchSubtitle(context,constraintSet,searchExplanationTitle.id)
            constraintLayout.addView(searchExplanationSubtitle)
            // add views
            constraintSet.applyTo(constraintLayout)
        }
        private fun searchScreenResultsSearchTitle(context: Context,constraintSet: ConstraintSet):AppCompatTextView{
            val searchTitle=context.getString(R.string.search_explanation)
            val appCompatTextView = AppCompatTextView(context).apply {
                id=ViewCompat.generateViewId()
                text=searchTitle
                setTextColor(Color.WHITE)
                setTextAppearance(R.style.TextAppearance_Asobi_Headline4)
            }
            val appCompatTextViewId= appCompatTextView.id
            constraintSet.setViewLayoutParams(appCompatTextViewId, ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)
            constraintSet constrainTopToParent appCompatTextViewId
            constraintSet constrainEndToParent  appCompatTextViewId
            constraintSet constrainStartToParent appCompatTextViewId
            constraintSet constrainBottomToParent appCompatTextViewId

            return appCompatTextView
        }
        private fun searchScreenResultsSearchSubtitle(context: Context,
                                                      constraintSet: ConstraintSet, searchTitleTextId:Int):AppCompatTextView{
            val searchResultsScreenSubtitle = context.getString(R.string.search_explanation_subtitle)
            val appCompatTextView = AppCompatTextView(context).apply {
                id=ViewCompat.generateViewId()
                text =searchResultsScreenSubtitle
                setTextColor(Color.WHITE)
                setTextAppearance(R.style.TextAppearance_Asobi_Subtitle1)
            }
            val searchSubtitleId=appCompatTextView.id
            constraintSet.setViewLayoutParams(searchSubtitleId,ConstraintSet.WRAP_CONTENT, ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(searchSubtitleId, marginTop = toolbarStartMargin.dp)
            constraintSet constrainStartToParent searchSubtitleId
            constraintSet constrainEndToParent searchSubtitleId
            constraintSet.connect(searchSubtitleId,ConstraintSet.TOP, searchTitleTextId,ConstraintSet.BOTTOM)
            return appCompatTextView
        }
    }
    /* End: Fragment view */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(enabled = true) {
            if (::searchResultsScreenToolbar.isInitialized){
                if (toolbarExpanded.not()){
                    mainActivityViewModel.openComicsGenreScreenFromSearchScreen()
                }else{
                    toolbarExpanded=false // back to default setting when back button is clicked
                    changeToolbarLayoutMarginOnClick()
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView = ComicsSearchResultsView(requireContext())
        searchResultsScreenToolbar = fragmentView.searchResultsToolbar
        searchResultsScreenSearchTitle = fragmentView.searchExplanationTitle
        searchResultsScreenSearchSubtitle = fragmentView.searchExplanationSubtitle
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       changeToolbarLayoutMarginOnClick()
    }
    private fun changeToolbarLayoutMarginOnClick(){
        with(searchResultsScreenToolbar){
            val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
            val expandedDrawable = shapeDrawable.apply { paint.color=resources.getColor(R.color.davy_grey,null)}
            setOnClickListener {
                toolbarExpanded = if (toolbarExpanded.not()){
                    val animator= expandCollapseMarginAnimation(toolbarStartMargin, toolbarEndMargin)
                    animator.addUpdateListener {
                        val recentlyAnimatedValue=it.animatedValue as Int
                        doActionIfWeAreOnDebug { logger.i("recently animated margin value is $recentlyAnimatedValue") }
                        marginLayoutParams.setMargins(recentlyAnimatedValue.dp)
                        layoutParams = marginLayoutParams
                    }
                    animator.doOnEnd { background = expandedDrawable }
                    animator.start()
                    true
                }else{
                    val animator= expandCollapseMarginAnimation(toolbarEndMargin, toolbarStartMargin)
                    animator.addUpdateListener {
                        val recentlyAnimatedValue=it.animatedValue as Int
                        doActionIfWeAreOnDebug { logger.i("recently animated margin value is $recentlyAnimatedValue") }
                        marginLayoutParams.setMargins(recentlyAnimatedValue.dp)
                        layoutParams = marginLayoutParams
                    }
                    animator.doOnEnd { background=resources.getDrawable(R.drawable.toolbar_bg,null)}
                    animator.start()
                    false
                }
            }
        }
    }
    /* Start: Fragment's specific utility methods */
    private fun expandCollapseMarginAnimation(start:Int,end:Int,
                                              animationDuration:Long=250, animationInterpolator:TimeInterpolator=AccelerateDecelerateInterpolator()):ValueAnimator = ValueAnimator.ofInt(start,end).apply {
            interpolator = animationInterpolator
            duration=animationDuration
        }
    private fun showKeyboard(view: View) {
        WindowInsetsControllerCompat(requireActivity().window,view).show(WindowInsetsCompat.Type.ime())
    }

    private fun dismissKeyboard(view: View) {
        WindowInsetsControllerCompat(requireActivity().window,view).hide(WindowInsetsCompat.Type.ime())
    }
    /* End: Fragment's specific utility methods */
    companion object{
        private const val toolbarStartMargin=16
        private const val toolbarEndMargin=0
        private const val noElevationValue=0f
        val shapeDrawable = ShapeDrawable(RectShape()).apply {
            this.setPadding(0,0,0,0)}

    }
}
package com.gibsonruitiari.asobi.ui.discovercomics

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.StatusBarScrimBehavior
import com.gibsonruitiari.asobi.utilities.extensions.applyMargin
import com.gibsonruitiari.asobi.utilities.extensions.constrainTopToParent
import com.gibsonruitiari.asobi.utilities.extensions.constrainStartToParent
import com.gibsonruitiari.asobi.utilities.extensions.constrainEndToParent
import com.gibsonruitiari.asobi.utilities.extensions.setViewLayoutParams
import com.gibsonruitiari.asobi.utilities.extensions.dp
import com.gibsonruitiari.asobi.utilities.widgets.ErrorStateLayout
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import java.time.LocalTime


class DiscoverFragmentView constructor(context:Context):CoordinatorLayout(context) {

    var appBarLayout:AppBarLayout
    var appBarViewScrim:View
    var loadingLayout:LoadingLayout
    var errorLayout:ConstraintLayout
    lateinit var notificationsButton:AppCompatImageButton
    lateinit var settingsButton:AppCompatImageButton
    lateinit var latestComicsRecyclerView: RecyclerView
    lateinit var latestComicsMoreText:AppCompatTextView
    lateinit var popularComicsRecyclerView:RecyclerView
    lateinit var popularComicsMoreText:AppCompatTextView
    lateinit var completedComicsRecyclerView: RecyclerView
    lateinit var completedComicsMoreText:AppCompatTextView


    init {
       layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
       ViewGroup.LayoutParams.MATCH_PARENT)
        fitsSystemWindows=true
        id = ViewCompat.generateViewId()
        background = resources.getDrawable(R.color.matte,null)
        appBarViewScrim = discoverFragmentStatusBarViewScrim(this.context)
        addView(appBarViewScrim)
        loadingLayout = discoverFragmentLoadingLayout(this.context) as LoadingLayout
        addView(loadingLayout)
        errorLayout = ErrorStateLayout(this.context).apply { visibility=View.GONE }
        addView(errorLayout)
        appBarLayout = discoverFragmentAppBarLayout(this.context).apply { visibility=View.GONE }
        addView(appBarLayout)
        val nestedScrollView =discoverFragmentNestedScrollView(this.context).apply { visibility=View.GONE }
        addView(nestedScrollView)
    }
    private fun discoverFragmentAppBarLayout(context: Context):AppBarLayout{
        val appBarLayout = AppBarLayout(context)
        with(appBarLayout){
            id = ViewCompat.generateViewId()
            fitsSystemWindows=true
            elevation=0f
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val toolbar=discoverFragmentToolbar(appBarLayout.context)
        appBarLayout.addView(toolbar)
        return appBarLayout
    }
    private fun discoverFragmentToolbar(context: Context):Toolbar{
        val toolbar = Toolbar(context).apply {
            id =ViewCompat.generateViewId()
            elevation=0f
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,140.dp)
            background=resources.getDrawable(R.drawable.discover_screen_gradient,null)
        }
       val intermediaryLayout= discoverFragmentToolbarIntermediaryLinearLayout(toolbar.context)
        toolbar.addView(intermediaryLayout)
        return toolbar
    }
    /* Toolbar does not allow/have options to give weight so we use a linear layout to do that */
    private fun discoverFragmentToolbarIntermediaryLinearLayout(context: Context):LinearLayout{
        val linearLayout = LinearLayout(context).apply {
            layoutParams= LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
            id=ViewGroup.generateViewId()
            showDividers =LinearLayout.SHOW_DIVIDER_MIDDLE
            dividerPadding =8.dp

            dividerDrawable=resources.getDrawable(com.google.android.material.R.drawable.abc_list_divider_material,null)
            orientation=LinearLayout.HORIZONTAL
        }
        val appCompatTextView = AppCompatTextView(linearLayout.context).apply {
            layoutParams =LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
            (layoutParams as LinearLayout.LayoutParams).weight =2f
            text=discoverScreenGreetingMessage()
            id=ViewCompat.generateViewId()
            /* In api 23> we do not have to set/give a context  */
            setTextAppearance(R.style.TextAppearance_Asobi_Headline4)
        }
        linearLayout.addView(appCompatTextView)

        val notificationButtonParams = ToolbarImageButtonParams(drawable = R.drawable.notification_bell,buttonContentDescription = resources.getString(R.string.notifications_button))
        notificationsButton = toolbarImageButtons(linearLayout.context,notificationButtonParams)
        linearLayout.addView(notificationsButton)

        val settingsButtonParams = ToolbarImageButtonParams(drawable = R.drawable.settings_icon,
        buttonContentDescription = resources.getString(R.string.settings_button))
         settingsButton=toolbarImageButtons(linearLayout.context,settingsButtonParams)
        linearLayout.addView(settingsButton)
        return linearLayout
    }
    private fun toolbarImageButtons(context: Context,params:ToolbarImageButtonParams):AppCompatImageButton = AppCompatImageButton(context).apply {
        val (drawable,buttonContentDescription,gravity,weight,buttonImageScaleType,buttonWidth,buttonHeight) = params
        id=ViewCompat.generateViewId()
            layoutParams = LinearLayout.LayoutParams(buttonWidth,buttonHeight.dp)
            (layoutParams as LinearLayout.LayoutParams).gravity = gravity
            (layoutParams as LinearLayout.LayoutParams).setMargins(R.dimen.margin_small)
            (layoutParams as LinearLayout.LayoutParams).weight=weight
            background=resources.getDrawable(R.color.transparent,null)
            scaleType = buttonImageScaleType
            setImageResource(drawable)
            contentDescription=buttonContentDescription
        }
    data class ToolbarImageButtonParams(@DrawableRes val drawable: Int,
    val buttonContentDescription: String, val gravity: Int=Gravity.END,val weight: Float=1f,
    val buttonImageScaleType:ImageView.ScaleType=ImageView.ScaleType.CENTER_INSIDE,
    val buttonWidth: Int=0,val buttonHeight:Int=30)

    private fun discoverFragmentStatusBarViewScrim(context: Context):View{
        return View(context).apply {
            id=ViewCompat.generateViewId()
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0.dp)
            background=resources.getDrawable(R.color.transparent, null)
            (layoutParams  as LayoutParams).behavior = StatusBarScrimBehavior(context)
            fitsSystemWindows=true
            (layoutParams as LayoutParams).gravity =Gravity.TOP
        }
    }
    private fun discoverFragmentNestedScrollView(context: Context):NestedScrollView{
      val nestedScrollView = NestedScrollView(context).apply {
          id=ViewCompat.generateViewId()
          layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT)
          isFillViewport=true
      }
        val constraintLayout = discoverFragmentConstraintLayout(nestedScrollView.context)
        nestedScrollView.addView(constraintLayout)
        return nestedScrollView
    }
    private fun discoverFragmentConstraintLayout(context: Context):ConstraintLayout{
        val constraintLayout =ConstraintLayout(context).apply {
            id=ViewCompat.generateViewId()
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
            (layoutParams as LayoutParams).setMargins(marginLeft,
            143.dp,marginRight,marginBottom)
            (layoutParams as LayoutParams).behavior =AppBarLayout.ScrollingViewBehavior()
        }


        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val (latestComicsLabel, latestComicsMoreLabel)=discoverFragmentLabelAndSubtitle(constraintLayout.context)
         latestComicsRecyclerView = discoverFragmentRecyclerView(constraintLayout.context)
        latestComicsMoreText = latestComicsMoreLabel

        popularComicsRecyclerView = discoverFragmentRecyclerView(constraintLayout.context)
        val (popularComicsLabel, popularComicsMoreLabel) = discoverFragmentLabelAndSubtitle(constraintLayout.context)
        popularComicsMoreText=  popularComicsMoreLabel

        completedComicsRecyclerView = discoverFragmentRecyclerView(constraintLayout.context)
        val (completedComicsLabel,completedComicsMoreLabel) = discoverFragmentLabelAndSubtitle(constraintLayout.context)
        completedComicsMoreText = completedComicsMoreLabel

        constraintLayout.addView(latestComicsLabel)
        constraintLayout.addView(latestComicsMoreText)
        constraintLayout.addView(latestComicsRecyclerView)
        constraintLayout.addView(popularComicsMoreText)
        constraintLayout.addView(popularComicsLabel)
        constraintLayout.addView(popularComicsRecyclerView)
        constraintLayout.addView(completedComicsLabel)
        constraintLayout.addView(completedComicsMoreText)
        constraintLayout.addView(completedComicsRecyclerView)

        // set the width and height first

        constraintSet.setViewLayoutParams(latestComicsLabel.id,ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(popularComicsLabel.id,ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)

        constraintSet.setViewLayoutParams(latestComicsMoreText.id, ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(popularComicsMoreText.id,ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)

        constraintSet.setViewLayoutParams(completedComicsMoreText.id,ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(completedComicsLabel.id,ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)


        constraintSet.setViewLayoutParams(latestComicsRecyclerView.id,ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(popularComicsRecyclerView.id,ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(completedComicsRecyclerView.id,ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.WRAP_CONTENT)


        // set the margin
        constraintSet.applyMargin(latestComicsLabel.id, marginEnd = 0, marginStart = 8.dp)
        constraintSet.applyMargin(latestComicsMoreText.id, marginStart = 0, marginEnd = 8.dp)
        constraintSet.applyMargin(popularComicsLabel.id,marginEnd=0, marginStart = 8.dp, marginTop = 20.dp)
        constraintSet.applyMargin(popularComicsMoreText.id,marginStart=0, marginEnd = 8.dp, marginTop = 20.dp)
        constraintSet.applyMargin(completedComicsLabel.id,marginEnd=0, marginStart = 8.dp, marginTop = 20.dp)
        constraintSet.applyMargin(completedComicsMoreText.id,marginStart=0, marginEnd = 8.dp, marginTop = 20.dp)

        constraintSet.applyMargin(popularComicsRecyclerView.id,marginEnd = 8.dp, marginStart = 8.dp, marginTop = 16.dp)
        constraintSet.applyMargin(latestComicsRecyclerView.id, marginEnd = 8.dp, marginStart = 8.dp, marginTop = 16.dp)
        constraintSet.applyMargin(completedComicsRecyclerView.id, marginEnd = 8.dp, marginStart = 8.dp, marginTop = 16.dp)


        // connect the views

        constraintSet constrainEndToParent latestComicsMoreText.id
        constraintSet constrainTopToParent latestComicsMoreText.id
        constraintSet constrainTopToParent latestComicsLabel.id
        constraintSet constrainStartToParent latestComicsLabel.id
        constraintSet.connect(latestComicsLabel.id,ConstraintSet.BOTTOM,latestComicsRecyclerView.id,ConstraintSet.TOP)

        constraintSet.connect(popularComicsLabel.id,ConstraintSet.TOP,latestComicsRecyclerView.id, ConstraintSet.BOTTOM)
        constraintSet.connect(popularComicsMoreText.id,ConstraintSet.TOP,latestComicsRecyclerView.id,ConstraintSet.BOTTOM)

        constraintSet constrainStartToParent  popularComicsLabel.id
        constraintSet constrainEndToParent  popularComicsMoreText.id

        constraintSet constrainStartToParent latestComicsRecyclerView.id
        constraintSet constrainEndToParent  latestComicsRecyclerView.id
        constraintSet.connect(latestComicsRecyclerView.id,ConstraintSet.TOP,latestComicsLabel.id,ConstraintSet.BOTTOM)

        constraintSet constrainStartToParent popularComicsRecyclerView.id
        constraintSet constrainEndToParent  popularComicsRecyclerView.id
        constraintSet.connect(popularComicsRecyclerView.id,ConstraintSet.TOP,popularComicsMoreText.id,ConstraintSet.BOTTOM)

        constraintSet constrainStartToParent  completedComicsLabel.id
        constraintSet constrainEndToParent  completedComicsMoreText.id
        constraintSet.connect(completedComicsLabel.id,ConstraintSet.TOP,popularComicsRecyclerView.id,ConstraintSet.BOTTOM)
        constraintSet.connect(completedComicsMoreText.id,ConstraintSet.TOP,popularComicsRecyclerView.id,ConstraintSet.BOTTOM)

        constraintSet constrainStartToParent completedComicsRecyclerView.id
        constraintSet constrainEndToParent  completedComicsRecyclerView.id
        constraintSet.connect(completedComicsRecyclerView.id,ConstraintSet.TOP,completedComicsLabel.id,ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintLayout)
        return constraintLayout
    }
    private fun discoverFragmentLoadingLayout(context: Context):FrameLayout = LoadingLayout(context).apply {
            id=ViewCompat.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        }

    private fun discoverFragmentLabelAndSubtitle(context: Context):Pair<AppCompatTextView,AppCompatTextView>{
        val comicsLabel=AppCompatTextView(context).apply {
            id=ViewCompat.generateViewId()
            gravity=Gravity.CENTER
            setTextColor(R.color.white)
            isAllCaps=false
            setTextAppearance(R.style.TextAppearance_Asobi_Label)
        }
        val moreLabel = AppCompatTextView(context).apply {
            id=ViewCompat.generateViewId()
            gravity=Gravity.CENTER
            isAllCaps=false
            setTextColor(R.color.white)
            setTextAppearance(R.style.TextAppearance_Asobi_Label)
        }
        return comicsLabel  to moreLabel
    }
    private fun discoverFragmentRecyclerView(context: Context,
    ):RecyclerView =  RecyclerView(context).apply {
        id=ViewCompat.generateViewId()
    }

    private fun discoverScreenGreetingMessage():String{
        val hour by lazy { LocalTime.now().hour }
        return when{
            hour<12 -> "Good morning"
            hour <17 ->"Good evening"
            else->"Good evening"
        }
    }


}
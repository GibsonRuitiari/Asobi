package com.gibsonruitiari.asobi.ui.discovercomics

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.setMargins
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.dp
import com.google.android.material.appbar.AppBarLayout
import java.time.LocalTime


class DiscoverFragmentView constructor(context:Context):CoordinatorLayout(context) {
  private fun discoverScreenGreetingMessage():String{
      val hour by lazy { LocalTime.now().hour }
     return when{
        hour<12 -> "Good morning"
         hour <17 ->"Good evening"
         else->"Good evening"
     }
  }
    var appBarLayout:AppBarLayout
    var appBarViewScrim:View
    lateinit var notificationsButton:AppCompatImageButton
    lateinit var settingsButton:AppCompatImageButton
    init {
       layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
       ViewGroup.LayoutParams.MATCH_PARENT)
        fitsSystemWindows=true
        id = ViewCompat.generateViewId()
        background = resources.getDrawable(R.color.matte,null)
        appBarViewScrim = discoverFragmentStatusBarViewScrim(this.context)
        addView(appBarViewScrim)
        appBarLayout = discoverFragmentAppBarLayout(this.context)
        addView(appBarLayout)
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
            background=resources.getDrawable(R.color.aqua_blue, null)
            fitsSystemWindows=true
            (layoutParams as LayoutParams).gravity =Gravity.TOP
        }
    }
}
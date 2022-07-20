package com.gibsonruitiari.asobi.utilities.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.gibsonruitiari.asobi.ui.uiModels.UiMeasureSpec
import com.google.android.material.snackbar.Snackbar


fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    anchor: View? = null
) {
    Snackbar.make(this, message, duration)
        .setAnchorView(anchor)
        .show()
}

fun View.showSnackBar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    anchor: View? = null
) {
    Snackbar.make(this, textId, duration)
        .setAnchorView(anchor)
        .show()
}

fun View.showSnackBar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    @IdRes anchor: Int
) {
    Snackbar.make(this, textId, duration)
        .setAnchorView(anchor)
        .show()
}
/* Ensure direct child of constraint layout take the maximum width as possible
* Useful mostly in cases where the screen size increases - screen size> Screen.COMPACT */
fun setContentToMaxWidth(view: View){
    val parent = view.parent as? ConstraintLayout ?: return
    val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
    val screenDensity = view.resources.displayMetrics.density
    val widthDp = parent.width /screenDensity
    val widthPercent = getContentMaxWidthPercent(widthDp.toInt())
    layoutParams.matchConstraintPercentWidth=widthPercent
    view.requestLayout()
}
private fun getContentMaxWidthPercent(maxWidthDp:Int):Float{
    return when{
        maxWidthDp >= 1024 -> 0.6f
        maxWidthDp >= 840 -> 0.7f
        maxWidthDp >= 600 -> 0.8f
        else->1f
    }
}
fun View.applyBottomInsets(){
    doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
        val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
            .ime())
        view.updatePadding(bottom= viewPaddingState.bottom + systemInsets.bottom)
    }
}
fun View.doOnApplyWindowInsets(f:(View,WindowInsetsCompat,ViewPaddingState)->Unit){
    val paddingState = createPaddingStateWhenGiven(this)
    ViewCompat.setOnApplyWindowInsetsListener(this){
        v,insets->
        f(v,insets,paddingState)
        insets
    }
}
fun View.requestApplyInsetsWhenAttached(){
    if (isAttachedToWindow){
        requestApplyInsets()
    }else{
        addOnAttachStateChangeListener(object :View.OnAttachStateChangeListener{
            override fun onViewAttachedToWindow(v: View?) {
                v?.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View?) =Unit
        })
    }
}
private fun createPaddingStateWhenGiven(view: View) = ViewPaddingState(
    view.paddingLeft,
    view.paddingTop,
    view.paddingRight,
    view.paddingBottom,
    view.paddingStart,
    view.paddingEnd
)
data class ViewPaddingState(val left:Int,val top:Int, val right:Int,val bottom:Int,
val start:Int, val end:Int)
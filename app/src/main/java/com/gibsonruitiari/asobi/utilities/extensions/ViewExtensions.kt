package com.gibsonruitiari.asobi.utilities.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
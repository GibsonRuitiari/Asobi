package com.gibsonruitiari.asobi.utilities.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LayoutAnimationController
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
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


fun View.fade(target:Float,transitionDuration:Long=250,transitionDelay: Long=0, animationInterpolator: Interpolator = LinearInterpolator(),
endAction:(View.()->Unit)?=null): Animator {
    val view_ = this
    return  ObjectAnimator.ofFloat(this,"alpha",target).apply {
        duration = transitionDuration
        startDelay = transitionDelay
        interpolator = animationInterpolator
        addUpdateListener {
           view_.alpha= it.animatedValue as Float
        }
        doOnEnd {
            endAction?.invoke(view_)
            visibility =if(target==0f) View.GONE else View.VISIBLE }
    }
}
fun RecyclerView.animate(animation:LayoutAnimationController){
    layoutAnimation= animation
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
fun View.applyBottomWindowInsets(paddingBottom: Int=0){
    doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
        val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
            .ime())
        view.updatePadding(bottom= viewPaddingState.bottom + systemInsets.bottom+paddingBottom)
    }
}
fun View.applyStartWindowInsets(paddingStart:Int=0){
    doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
        val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        view.updatePadding(left= viewPaddingState.start + systemInsets.left+paddingStart)
    }
}
fun View.applyEndWindowInsets(paddingEnd:Int=0){
    doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
        val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
            .ime())
        view.updatePadding(right= viewPaddingState.end + systemInsets.right+paddingEnd)
    }
}
fun View.applyTopWindowInsets(paddingTop:Int=0){
    doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
        val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
            .ime())
        view.updatePadding(top= viewPaddingState.top + systemInsets.top+paddingTop)
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
package com.gibsonruitiari.asobi.utilities.extensions

import android.content.res.Resources
import android.graphics.Path
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.animation.Interpolator
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.roundToInt


fun createPathInterpolator(controlArray:FloatArray): Interpolator {
    require(controlArray.size==4){"control array size must be greater than 5"}
    return PathInterpolatorCompat.create(controlArray[0],controlArray[1],controlArray[2],controlArray[3])
}
fun createPathInterpolator(interpolatorPath: Path): Interpolator = PathInterpolatorCompat.create(interpolatorPath)

fun Job?.cancelIfActive(){
   this?.let {
       if (it.isActive) it.cancel()
   }
}


/*  Utility methods for our ConstraintLayout container */
 fun ConstraintSet.setViewLayoutParams(viewId:Int, width:Int, height:Int){
    constrainHeight(viewId, height)
    constrainWidth(viewId, width)
}
 fun ConstraintSet.applyMargin(viewId: Int, marginStart:Int=0,
                                      marginEnd:Int=0, marginBottom:Int=0, marginTop:Int=0){
    setMargin(viewId, ConstraintSet.START,marginStart)
    setMargin(viewId, ConstraintSet.END,marginEnd)
    setMargin(viewId, ConstraintSet.TOP,marginTop)
    setMargin(viewId, ConstraintSet.BOTTOM,marginBottom)
}
 infix fun ConstraintSet.constrainStartToParent(viewId: Int){
    connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
}
 infix fun ConstraintSet.constrainTopToParent(viewId: Int){
    connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
}
 infix fun ConstraintSet.constrainBottomToParent(viewId: Int){
    connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
}
 infix fun ConstraintSet.constrainEndToParent(viewId: Int){
    connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
}

internal class SafeClickListener(private var defaultInterval:Int=1000,
private val onSafeClick:(View)->Unit):View.OnClickListener{
    private var lastTimeClicked:Long=0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime()-lastTimeClicked<defaultInterval){
            return
        }
        lastTimeClicked=SystemClock.elapsedRealtime()
        onSafeClick(v)
    }
}
fun View.setOnSafeClickListener(onSafeClick: (View) -> Unit){
    val safeClickListener = SafeClickListener{
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}
fun Throwable.parseThrowableErrorMessageIntoUsefulMessage():String = when(this){
    is UnknownHostException, is SocketTimeoutException, is ConnectException ->{
        "Loading of comics failed due to your internet connection;please check your connection and try again"
    }
    else->"Loading of comics failed due to the following error ${this.message}. ${System.lineSeparator()} Please try again after sometime"
}

val screenWidth = Resources.getSystem().displayMetrics.run { widthPixels/density }


inline fun Fragment.launchAndRepeatWithViewLifecycle(minActiveState:Lifecycle.State=Lifecycle.State.STARTED,
crossinline block:suspend CoroutineScope.()->Unit):Job = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState){
            block()
        }
    }

fun Fragment.changeStatusBarToTransparentInFragment(@ColorInt color:Int){
    val window= requireActivity().window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor=color
}


val Int.dp:Int
get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
val Float.dp:Int
get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

inline fun doActionIfWeAreOnDebug(crossinline action:()->Unit){
    if (BuildConfig.DEBUG){ action() }
}

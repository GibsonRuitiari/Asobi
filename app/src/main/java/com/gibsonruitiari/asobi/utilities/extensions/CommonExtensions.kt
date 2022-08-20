package com.gibsonruitiari.asobi.utilities.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Path
import android.os.Parcel
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.os.ParcelCompat
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.BuildConfig
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
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

fun Context.isTablet(): Boolean {
    return resources.configuration.smallestScreenWidthDp >= 720
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
class RetryTrigger{
    enum class STATE{RETRYING,IDLE}
    val retryEvent= MutableStateFlow(STATE.RETRYING)
    fun retry()  {
        retryEvent.value = STATE.RETRYING
    }
}
@OptIn(FlowPreview::class)
fun <T> retryFlow(retryTrigger: RetryTrigger, source:()->Flow<T>) = retryTrigger.retryEvent.filter { it==RetryTrigger.STATE.RETRYING }
        .flatMapConcat { source() }
        .onEach { retryTrigger.retryEvent.value=RetryTrigger.STATE.IDLE }

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
/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}
inline fun Fragment.launchAndRepeatWithViewLifecycle(minActiveState:Lifecycle.State=Lifecycle.State.STARTED,
crossinline block:suspend CoroutineScope.()->Unit):Job = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState){
            block()
        }
    }
inline fun Fragment.changeStatusBarToTransparentInFragment(@ColorInt color:Int){
    val window= requireActivity().window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor=color
}


val Int.dp:Int
get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
val Float.dp:Int
get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

fun Fragment.resourcesInstance():Resources = requireActivity().resources

inline fun doActionIfWeAreOnDebug(action:()->Unit){
    if (BuildConfig.DEBUG){ action() }
}
/**
 * Map a slideOffset (in the range `[-1, 1]`) to an alpha value based on the desired range.
 * For example, `slideOffsetToAlpha(0.5, 0.25, 1) = 0.33` because 0.5 is 1/3 of the way between
 * 0.25 and 1. The result value is additionally clamped to the range `[0, 1]`.
 */
fun slideOffsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
    return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
}

/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

/** Write a boolean to a Parcel. */
fun Parcel.writeBooleanUsingCompat(value: Boolean) = ParcelCompat.writeBoolean(this, value)

/** Read a boolean from a Parcel. */
fun Parcel.readBooleanUsingCompat() = ParcelCompat.readBoolean(this)
package com.gibsonruitiari.asobi.utilities.extensions

import android.content.res.Resources
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.ParcelCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.BuildConfig
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.roundToInt

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
crossinline block:suspend CoroutineScope.()->Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState){
            block()
        }
    }
}
fun<E> SendChannel<E>.tryOffer(element:E):Boolean = try{
    trySend(element).isSuccess
    true
}catch (_:Exception){
    false
}
inline  fun FragmentManager.setFragmentToBeShownToTheUser(logger: Logger,
                                                          selectedFragment: Fragment,
fragmentsArray:ArrayList<Fragment>, updateCurrentFragmentIndex:(index:Int)->Unit){
    var fragmentTransaction = beginTransaction()
    fragmentsArray.forEachIndexed { index, fragment ->
        if (selectedFragment == fragment){
            fragmentTransaction = fragmentTransaction.show(fragment)
            updateCurrentFragmentIndex(index)
            doActionIfWeAreOnDebug { logger.i("current shown fragment is ${fragment.tag}") }
        }else{
            fragmentTransaction = fragmentTransaction.hide(fragment)
        }
    }
    fragmentTransaction.commit()
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
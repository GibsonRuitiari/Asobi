package com.gibsonruitiari.asobi.utilities.extensions

import android.content.res.Resources
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.ParcelCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.ui.MainFragment
import com.gibsonruitiari.asobi.ui.uiModels.UiMeasureSpec
import com.gibsonruitiari.asobi.utilities.ScreenSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
private  const val  defaultNumberOfColumns =2
private const val defaultSpacing = 4
private const val mediumNumberOfColumns = 4
private const val mediumSpacing = 8
private const val extendedNumberOfColumns = 6
private const val extendedSpacing = 12
/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}
inline fun Fragment.launchAndRepeatWithViewLifecycle(minActiveState
                                                     :Lifecycle.State=Lifecycle.State.STARTED,
crossinline block:suspend CoroutineScope.()->Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState){
            block()
        }
    }
}
fun Fragment.resourcesInstance():Resources = requireContext().resources
fun ScreenSize.constructUiMeasureSpecFromScreenSize()=when(this){
    ScreenSize.COMPACT->{
        /* layout grid uses 4 columns so for the recycler view's grid layout we need 2 columns */
        /* layout grid uses 16.dp gutter for 4 columns so for the recycler view's grid layout spacing in between columns ought to be 4.dp */
        UiMeasureSpec(recyclerViewColumns = defaultNumberOfColumns, recyclerViewMargin = defaultSpacing)
    }
    ScreenSize.MEDIUM->{
        /* layout grid uses 8 columns so for the recycler view's grid layout we need 4 columns */
        /* layout grid uses 24.dp gutter for 8 columns so for the recycler view's grid layout spacing in between columns ought to be 8.dp */
        UiMeasureSpec(recyclerViewColumns = mediumNumberOfColumns, recyclerViewMargin = mediumSpacing)
    }
    ScreenSize.EXPANDED->{
        /* layout grid uses 12 columns so for the recycler view's grid layout we need 6 columns */
        UiMeasureSpec(recyclerViewColumns = extendedNumberOfColumns, recyclerViewMargin = extendedSpacing)
    }
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
package com.gibsonruitiari.asobi.utilities

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import com.gibsonruitiari.asobi.utilities.extensions.dp

/**
 * File contains default padding, margin and used across the app and other
 * ui values needed
 */
val noMargin:Int=0.dp
val marginSmall:Int =8.dp
val marginMedium=16.dp
val marginLarge =24.dp

val noPadding=0.dp
val paddingSmall =8.dp
val paddingMedium=16.dp
val paddingLarge=24.dp

val defaultIconSize=24.dp
val mediumIconSize =32.dp
val largeIconSize =40.dp

val noElevation =0f
val smallElevation=4f
val mediumElevation=8f
val largeElevation=12f

val defaultButtonHeight =65.dp

val normalTextSize=16f
val mediumTextSize =24f
val largeTextSize =32f

val fullAlpha=1f
val noAlpha=0f

val easeOutInterpolatorArray = floatArrayOf(0f,0f,0.58f,1f)
private val defaultButtonColorPressState = intArrayOf(Color.GRAY,Color.BLACK) // pressed, -pressed
private val defaultButtonPressedState = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(-android.R.attr.state_pressed))
val defaultColorStateList = ColorStateList(defaultButtonPressedState, defaultButtonColorPressState)
val defaultShapeDrawable= ShapeDrawable(RectShape()).apply { setPadding(0,0,0,0)}
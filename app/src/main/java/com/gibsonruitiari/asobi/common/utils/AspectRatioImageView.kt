package com.gibsonruitiari.asobi.common.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AspectRatioImageView @JvmOverloads constructor(context:Context,
attrs:AttributeSet?=null, defStyleAttr:Int=0):AppCompatImageView(context, attrs, defStyleAttr) {
    var aspectRatio:Double = -1.0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (aspectRatio==-1.0)  return // user has not set aspect ratio
        val width = measuredWidth
        val height = (width * aspectRatio).toInt()
        if(height == measuredHeight) return
        setMeasuredDimension(width,height)
    }
}


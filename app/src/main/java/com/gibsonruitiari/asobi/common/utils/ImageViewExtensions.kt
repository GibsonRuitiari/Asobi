package com.gibsonruitiari.asobi.common.utils

import androidx.appcompat.widget.AppCompatImageView



// ratio must be 3/2 so say w=250 h= 375
fun AspectRatioImageView.setAspectRatio(width:Int?,
height:Int?){
    if (width!=null && height!=null){
        aspectRatio = height.toDouble()/width.toDouble()
    }
}
package com.gibsonruitiari.asobi.common.extensions

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.gibsonruitiari.asobi.GlideApp
import com.gibsonruitiari.asobi.common.utils.AspectRatioImageView
import com.google.android.material.imageview.ShapeableImageView

const val CrossFadeDuration =400
fun ShapeableImageView.loadPhotoUrl(
    url: String,
    requestListener: RequestListener<Drawable>? = null
) {
    GlideApp.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(CrossFadeDuration))
        .addListener(requestListener)
        .into(this)
        .clearOnDetach()
}

// ratio must be 3/2 so say w=250 h= 375
fun AspectRatioImageView.setAspectRatio(width:Int?,
                                        height:Int?){
    if (width!=null && height!=null){
        aspectRatio = height.toDouble()/width.toDouble()
    }
}
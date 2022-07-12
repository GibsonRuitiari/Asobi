package com.gibsonruitiari.asobi.utilities.extensions

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.gibsonruitiari.asobi.di.GlideApp
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

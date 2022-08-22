package com.gibsonruitiari.asobi.utilities.views

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.google.android.material.button.MaterialButton

class ErrorStateLayout  constructor(context:Context):ConstraintLayout(context) {
    var errorTitle:AppCompatTextView
    var subtitleError:AppCompatTextView
    var retryButton:MaterialButton
    init {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        val errorImageView = errorLayoutImageView(this.context)
        addView(errorImageView)
        errorTitle = errorLayoutErrorTitle(this.context)
        addView(errorTitle)
        subtitleError = errorLayoutErrorSubtitleTitle(this.context)
        addView(subtitleError)
        retryButton=errorLayoutRetryMaterialButton(this.context)
        addView(retryButton)

        constraintSet.setViewLayoutParams(errorImageView.id,150.dp,150.dp)
        constraintSet.setViewLayoutParams(errorTitle.id,0.dp,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(subtitleError.id,0.dp,ConstraintSet.WRAP_CONTENT)
        constraintSet.setViewLayoutParams(retryButton.id,150.dp,60.dp)

        constraintSet.applyMargin(errorImageView.id, marginTop = 50.dp)
        constraintSet.applyMargin(errorTitle.id, marginTop = 8.dp, marginEnd = 24.dp, marginStart = 24.dp)
        constraintSet.applyMargin(subtitleError.id, marginTop = 16.dp, marginEnd = 24.dp, marginStart = 24.dp)
        constraintSet.applyMargin(retryButton.id, marginStart = 16.dp, marginEnd = 16.dp,marginTop=16.dp)

        constraintSet constrainStartToParent errorImageView.id
        constraintSet constrainTopToParent  errorImageView.id
        constraintSet constrainEndToParent  errorImageView.id
        //constraintSet constrainBottomToParent errorImageView.id
        constraintSet.connect(errorImageView.id,ConstraintSet.BOTTOM,errorTitle.id,ConstraintSet.TOP)

        constraintSet constrainStartToParent errorTitle.id
        constraintSet constrainEndToParent  errorTitle.id
        constraintSet.connect(errorTitle.id,ConstraintSet.TOP,errorImageView.id,ConstraintSet.BOTTOM)
        constraintSet.connect(errorTitle.id,ConstraintSet.BOTTOM,subtitleError.id,ConstraintSet.TOP)

        constraintSet constrainStartToParent subtitleError.id
        constraintSet constrainEndToParent  subtitleError.id
        constraintSet.connect(subtitleError.id,ConstraintSet.TOP, errorTitle.id,ConstraintSet.BOTTOM)
        constraintSet.connect(subtitleError.id,ConstraintSet.BOTTOM,retryButton.id,ConstraintSet.TOP)

        constraintSet constrainStartToParent retryButton.id
        constraintSet constrainEndToParent  retryButton.id
        constraintSet.connect(retryButton.id,ConstraintSet.TOP,subtitleError.id,ConstraintSet.BOTTOM)

        constraintSet.applyTo(this)
    }
    private fun errorLayoutErrorTitle(context: Context):AppCompatTextView{
        return AppCompatTextView(context).apply {
            id=ViewCompat.generateViewId()
            gravity=Gravity.CENTER
            textAlignment= TEXT_ALIGNMENT_CENTER
            setTextAppearance(R.style.TextAppearance_Asobi_ErrorTitle)
        }
    }
    private fun errorLayoutErrorSubtitleTitle(context: Context):AppCompatTextView{
        return AppCompatTextView(context).apply {
            id=ViewCompat.generateViewId()
            gravity=Gravity.CENTER
            textAlignment= TEXT_ALIGNMENT_CENTER
            setTextAppearance(R.style.TextAppearance_Asobi_ErrorSubTitle)
        }
    }
    private fun errorLayoutImageView(context: Context):AppCompatImageView{
        return AppCompatImageView(context).apply {
            id=ViewCompat.generateViewId()
            setImageResource(R.drawable.no_internet_connection_image)
            scaleType=ImageView.ScaleType.CENTER_CROP
            contentDescription=resources.getString(R.string.error_image)
        }
    }
    private fun errorLayoutRetryMaterialButton(context: Context):MaterialButton{
        return MaterialButton(context).apply {
            id=ViewCompat.generateViewId()
            setBackgroundColor(context.getColor(R.color.white))
            text=resources.getString(R.string.cd_retry)
            isAllCaps=false
            setTextColor(context.getColor(R.color.matte))
        }
    }

}
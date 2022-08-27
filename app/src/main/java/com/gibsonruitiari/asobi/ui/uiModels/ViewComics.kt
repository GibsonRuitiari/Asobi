package com.gibsonruitiari.asobi.ui.uiModels

import com.gibsonruitiari.asobi.ui.comicsadapters.Differentiable

data class ViewComics(val comicName:String, val comicThumbnail:String,
                      val comicLink:String, val latestIssue:String?=null): Differentiable {
    override val diffId: String
        get() = comicName+"-"+hashCode()
   }


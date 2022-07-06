package com.gibsonruitiari.asobi.presenter.uiModels

import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff.Differentiable

data class ViewComics(val comicName:String, val comicThumbnail:String,
                      val comicLink:String, val latestIssue:String?=null):Differentiable{
    override val diffId: String
        get() = comicName+"-"+hashCode()
                      }


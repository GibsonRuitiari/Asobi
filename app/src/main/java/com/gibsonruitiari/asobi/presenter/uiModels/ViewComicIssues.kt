package com.gibsonruitiari.asobi.presenter.uiModels

import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff.Differentiable

data class ViewComicIssues(val issueName:String,val issueLink:String,
val issueReleaseDate:String):Differentiable{
    override val diffId: String
        get() = issueName +"-"+hashCode()
}
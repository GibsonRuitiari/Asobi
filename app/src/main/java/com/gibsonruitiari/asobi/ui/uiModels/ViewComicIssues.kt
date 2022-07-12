package com.gibsonruitiari.asobi.ui.uiModels

import com.gibsonruitiari.asobi.ui.comicsadapters.Differentiable

data class ViewComicIssues(val issueName:String,val issueLink:String,
val issueReleaseDate:String): Differentiable {
    override val diffId: String
        get() = issueName +"-"+hashCode()
}
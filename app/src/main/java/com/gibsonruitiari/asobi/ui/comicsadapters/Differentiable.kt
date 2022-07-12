package com.gibsonruitiari.asobi.ui.comicsadapters
// to be implemented by objects that are supposed to be diffable basically data classes
interface Differentiable {
    val diffId:String
    fun areContentsTheSame(other: Differentiable):Boolean= this==other
    fun getChangePayload(other: Differentiable):Any?=null
}
package com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff
// to be implemented by objects that are supposed to be diffable basically data classes
interface Differentiable {
    val diffId:String
    fun areContentsTheSame(other:Differentiable):Boolean= this==other
    fun getChangePayload(other: Differentiable):Any?=null
}
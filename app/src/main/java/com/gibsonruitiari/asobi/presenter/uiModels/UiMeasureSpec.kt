package com.gibsonruitiari.asobi.presenter.uiModels

data class UiMeasureSpec(val recyclerViewColumns:Int,val recyclerViewMargin:Int){
    companion object{
        val default = UiMeasureSpec(2,16)
    }
}

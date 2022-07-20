package com.gibsonruitiari.asobi.ui.uiModels

data class UiMeasureSpec(val recyclerViewColumns:Int,val recyclerViewMargin:Int, val gutter:Int){
    companion object{
        val default = UiMeasureSpec(4,8,8)
    }
}

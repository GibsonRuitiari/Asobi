package com.gibsonruitiari.asobi.domain

import androidx.paging.DataSource

abstract class BaseDataSourceFactory<T:Any>:DataSource.Factory<Int, T>() {
    abstract fun createDataSource():BaseDataSource<T>

}
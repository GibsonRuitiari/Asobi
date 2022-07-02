package com.gibsonruitiari.asobi.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.network.NetworkState

abstract class BaseDataSource<T:Any> (private val logger: Logger): PagingSource<Int,T> (){
    abstract suspend fun loadData(page:Int,numberOfItemsToBeLoaded:Int):List<T>
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
       return state.anchorPosition?.let {
           state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
       }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
      return try {
        val pageNumber = params.key ?: 1
        val data = loadData(pageNumber, params.loadSize)
        val previousKey = if (pageNumber>0) pageNumber-1 else null
        val nextKey = if (data.isNotEmpty()) pageNumber.plus(1) else null
        LoadResult.Page(data = data, prevKey = previousKey, nextKey = nextKey)
    }catch (e:java.io.IOException){
        logger.e(e,"an error occurred while loading data in base data source :-[")
        LoadResult.Error(e)
      }
}
}
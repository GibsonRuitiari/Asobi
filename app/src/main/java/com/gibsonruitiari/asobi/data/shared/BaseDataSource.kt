package com.gibsonruitiari.asobi.data.shared

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.logging.Logger

abstract class BaseDataSource<T:Any> (private val logger: Logger): PagingSource<Int,T> (){
    abstract suspend  fun loadData(page:Int):List<T>
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
       return state.anchorPosition?.let {
           state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
       }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
      return try {
        val pageNumber = params.key ?: 1
        val data = loadData(pageNumber)
        doActionIfWeAreOnDebug {
            data.forEach {logger.i("comic link--> ${ (it as SManga).comicLink} comic Name--> ${(it as SManga).comicName} ") }
        }
        logger.i("fetched data $data")
        //  if (pageNumber>0) pageNumber-1 else null

        val previousKey = if (pageNumber==1 ) null else pageNumber-1
        val nextKey = if (data.isNotEmpty()) pageNumber.plus(1) else null
        LoadResult.Page(data = data, prevKey = previousKey, nextKey = nextKey)
    }catch (e:Exception){
        logger.e(e,"The following error occurred while fetching comics from this data source")
        LoadResult.Error(e)
      }
}
}
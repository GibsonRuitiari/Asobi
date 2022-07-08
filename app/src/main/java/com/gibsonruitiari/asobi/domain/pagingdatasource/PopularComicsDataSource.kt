package com.gibsonruitiari.asobi.domain.pagingdatasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.PopularComicsRepo
import kotlinx.coroutines.flow.firstOrNull

class PopularComicsDataSource(private val logger: Logger, private val popularComicsRepo: PopularComicsRepo): PagingSource<Int,SManga>() {

    override fun getRefreshKey(state: PagingState<Int, SManga>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SManga> {
        return try {
            val pageNumber = params.key ?: 1

            val data = popularComicsRepo.getPopularComics(pageNumber).firstOrNull() ?: emptyList()
            val previousKey = if (pageNumber>0) pageNumber-1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber.plus(1) else null
            LoadResult.Page(data = data, prevKey = previousKey, nextKey = nextKey)
        }catch (e:Exception){
            logger.e(e,"The following error occurred while loading popular comics ${e.message}")
            LoadResult.Error(e)
        }
}
}

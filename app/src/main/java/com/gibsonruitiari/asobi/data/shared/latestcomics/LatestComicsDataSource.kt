package com.gibsonruitiari.asobi.data.shared.latestcomics

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.BaseDataSource
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import kotlinx.coroutines.flow.firstOrNull

class LatestComicsDataSource(private val logger: Logger,
private val latestComicsRepo: LatestComicsRepo
): PagingSource<Int,SManga> () {

    override fun getRefreshKey(state: PagingState<Int, SManga>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SManga> {
        return try {
            val pageNumber = params.key ?: 1
            val data=latestComicsRepo.getLatestComics(pageNumber).firstOrNull() ?: emptyList()
            doActionIfWeAreOnDebug {
                data.forEach { logger.i("comic link--> ${it.comicLink} comic Name--> ${it.comicName} ") }
            }
            logger.i("fetched data $data")
            //  if (pageNumber>0) pageNumber-1 else null

            val previousKey = if (pageNumber == 1) null else pageNumber - 1
            val nextKey = if (data.isNotEmpty()) pageNumber.plus(1) else null
            LoadResult.Page(data = data, prevKey = previousKey, nextKey = nextKey)
        } catch (e: Exception) {
            logger.e(e, "The following error occurred while fetching comics from this data source")
            LoadResult.Error(e)
        }
    }

}


//BaseDataSource<SManga>(logger) {
//    override suspend fun loadData(page: Int): List<SManga> {
//        return latestComicsRepo.getLatestComics(page).firstOrNull() ?: emptyList()
//    }



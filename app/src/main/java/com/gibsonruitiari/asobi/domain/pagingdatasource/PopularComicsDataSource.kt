package com.gibsonruitiari.asobi.domain.pagingdatasource

import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.PopularComicsRepo
import com.gibsonruitiari.asobi.domain.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class PopularComicsDataSource(logger: Logger, private val popularComicsRepo: PopularComicsRepo):BaseDataSource<SManga>(logger){
    override suspend fun loadData(page: Int): List<SManga> =
        popularComicsRepo.getPopularComics(page).firstOrNull() ?: emptyList()

}

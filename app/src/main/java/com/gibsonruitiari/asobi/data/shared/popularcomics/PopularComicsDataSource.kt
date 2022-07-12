package com.gibsonruitiari.asobi.data.shared.popularcomics

import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class PopularComicsDataSource(logger: Logger, private val popularComicsRepo: PopularComicsRepo):
    BaseDataSource<SManga>(logger){
    override suspend fun loadData(page: Int): List<SManga> =
        popularComicsRepo.getPopularComics(page).firstOrNull() ?: emptyList()

}

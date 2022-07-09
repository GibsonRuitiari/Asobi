package com.gibsonruitiari.asobi.domain.pagingdatasource

import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.LatestComicsRepo
import com.gibsonruitiari.asobi.domain.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class LatestComicsDataSource(private val logger: Logger,
private val latestComicsRepo: LatestComicsRepo): BaseDataSource<SManga>(logger) {
    override suspend fun loadData(page: Int): List<SManga> {
        logger.i("Fetching latest comics")
        return latestComicsRepo.getLatestComics(page).firstOrNull() ?: emptyList()
    }
}
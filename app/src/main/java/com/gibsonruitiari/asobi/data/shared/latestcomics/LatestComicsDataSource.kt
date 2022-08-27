package com.gibsonruitiari.asobi.data.shared.latestcomics

import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class LatestComicsDataSource(private val logger: Logger,
private val latestComicsRepo: LatestComicsRepo
): BaseDataSource<SManga>(logger) {
    override suspend fun loadData(page: Int): List<SManga> {
        logger.i("on page $page -> Fetching latest comics")
        return latestComicsRepo.getLatestComics(page).firstOrNull() ?: emptyList()
    }
}
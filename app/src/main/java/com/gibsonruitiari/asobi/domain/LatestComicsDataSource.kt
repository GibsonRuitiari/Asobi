package com.gibsonruitiari.asobi.domain

import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.latestComics
import kotlinx.coroutines.flow.firstOrNull

class LatestComicsDataSource(private val logger: Logger):BaseDataSource<SManga>(logger) {
    override suspend fun loadData(page: Int): List<SManga> {
        logger.i("Fetching latest comics")
        return latestComics(page).firstOrNull()?.mangas ?: emptyList()
    }
}
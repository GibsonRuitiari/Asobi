package com.gibsonruitiari.asobi.domain.pagingdatasource

import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.CompletedComicsRepo
import com.gibsonruitiari.asobi.domain.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class CompletedComicsDataSource (private val logger: Logger, private val completedComicsRepo: CompletedComicsRepo): BaseDataSource<SManga>(logger) {
    override suspend fun loadData(page: Int): List<SManga> {
        logger.i("Fetching completed comics")
        return completedComicsRepo.getCompletedComics(page).firstOrNull() ?: emptyList()
    }
}
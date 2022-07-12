package com.gibsonruitiari.asobi.data.shared.completedcomics

import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class CompletedComicsDataSource (private val logger: Logger, private val completedComicsRepo: CompletedComicsRepo): BaseDataSource<SManga>(logger) {
    override suspend fun loadData(page: Int): List<SManga> {
        logger.i("Fetching completed comics")
        return completedComicsRepo.getCompletedComics(page).firstOrNull() ?: emptyList()
    }
}
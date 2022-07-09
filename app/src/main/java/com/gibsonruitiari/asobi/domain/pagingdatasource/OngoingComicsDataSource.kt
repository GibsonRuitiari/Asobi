package com.gibsonruitiari.asobi.domain.pagingdatasource

import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.OngoingComicsRepo
import com.gibsonruitiari.asobi.domain.BaseDataSource
import kotlinx.coroutines.flow.firstOrNull

class OngoingComicsDataSource (private val logger: Logger,
private val ongoingComicsRepo: OngoingComicsRepo): BaseDataSource<SManga>(logger) {
    override suspend fun loadData(page: Int): List<SManga> {
        logger.i("Fetching on going comics")
        return ongoingComicsRepo.getOngoingComics(page).firstOrNull() ?: emptyList()
    }
}
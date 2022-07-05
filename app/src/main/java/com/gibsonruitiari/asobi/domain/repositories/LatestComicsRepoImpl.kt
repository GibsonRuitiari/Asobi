package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.LatestComicsRepo
import kotlinx.coroutines.flow.Flow

class LatestComicsRepoImpl:LatestComicsRepo {
    override suspend fun getLatestComics(): Flow<List<SManga>> {
        comic
    }
}
package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.latestComics
import com.gibsonruitiari.asobi.data.repositories.LatestComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class LatestComicsRepoImpl:LatestComicsRepo {
    override suspend fun getLatestComics(page: Int): Flow<List<SManga>> {
        return flowOf(latestComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
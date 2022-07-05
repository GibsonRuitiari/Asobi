package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.latestComics
import com.gibsonruitiari.asobi.data.popularComics
import com.gibsonruitiari.asobi.data.repositories.PopularComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class PopularComicsRepoImpl: PopularComicsRepo {
    override suspend fun getPopularComics(page: Int): Flow<List<SManga>> {
        return flowOf(popularComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
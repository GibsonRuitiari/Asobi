package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.completedComics
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.latestComics
import com.gibsonruitiari.asobi.data.repositories.CompletedComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class CompletedComicsRepoImpl:CompletedComicsRepo {
    override suspend fun getCompletedComics(page: Int): Flow<List<SManga>> {
        return flowOf(completedComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
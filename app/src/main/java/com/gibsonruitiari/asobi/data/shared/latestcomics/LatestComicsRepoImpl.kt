package com.gibsonruitiari.asobi.data.shared.latestcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.latestComics
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class LatestComicsRepoImpl: LatestComicsRepo {
    override  fun getLatestComics(page: Int): Flow<List<SManga>> = flow {
         emit(latestComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
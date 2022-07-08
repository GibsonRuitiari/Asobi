package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.popularComics
import com.gibsonruitiari.asobi.data.repositories.PopularComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class PopularComicsRepoImpl: PopularComicsRepo {
    override  fun getPopularComics(page: Int): Flow<List<SManga>> = flow{
        val data = popularComics(page).firstOrNull()?.mangas ?: emptyList()
         emit(data)
    }
}
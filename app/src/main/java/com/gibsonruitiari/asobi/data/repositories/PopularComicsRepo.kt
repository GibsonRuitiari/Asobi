package com.gibsonruitiari.asobi.data.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface PopularComicsRepo {
    suspend fun getPopularComics(page:Int):Flow<List<SManga>>
}
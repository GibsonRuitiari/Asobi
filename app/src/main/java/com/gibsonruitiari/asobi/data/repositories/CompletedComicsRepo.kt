package com.gibsonruitiari.asobi.data.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface CompletedComicsRepo {
    suspend fun getCompletedComics(page:Int):Flow<List<SManga>>
}
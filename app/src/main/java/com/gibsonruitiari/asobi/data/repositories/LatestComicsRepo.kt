package com.gibsonruitiari.asobi.data.repositories

import com.gibsonruitiari.asobi.data.datamodels.MangaPage
import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface LatestComicsRepo {
    suspend fun getLatestComics(page:Int):Flow<List<SManga>>
}
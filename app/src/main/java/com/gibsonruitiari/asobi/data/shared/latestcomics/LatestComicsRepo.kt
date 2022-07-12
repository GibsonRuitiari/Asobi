package com.gibsonruitiari.asobi.data.shared.latestcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface LatestComicsRepo {
     fun getLatestComics(page:Int):Flow<List<SManga>>
}
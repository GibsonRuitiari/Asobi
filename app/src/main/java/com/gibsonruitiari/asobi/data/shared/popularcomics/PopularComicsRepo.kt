package com.gibsonruitiari.asobi.data.shared.popularcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface PopularComicsRepo {
     fun getPopularComics(page:Int):Flow<List<SManga>>
}
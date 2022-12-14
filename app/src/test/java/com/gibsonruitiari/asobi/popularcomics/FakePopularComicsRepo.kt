package com.gibsonruitiari.asobi.popularcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePopularComicsRepo: PopularComicsRepo {
    override fun getPopularComics(page: Int): Flow<List<SManga>>  {
        return if (page==1) flowOf(sampleComicList) else flowOf(emptyList())
    }
}
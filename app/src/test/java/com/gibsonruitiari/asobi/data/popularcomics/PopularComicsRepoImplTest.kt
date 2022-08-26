package com.gibsonruitiari.asobi.data.popularcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PopularComicsRepoImplTest: PopularComicsRepo {
    override fun getPopularComics(page: Int): Flow<List<SManga>> = flow {
        emit(sampleComicList)
    }
}
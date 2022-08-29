package com.gibsonruitiari.asobi.ongoingcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeOngoingComicsRepo:OngoingComicsRepo {
    override fun getOngoingComics(page: Int): Flow<List<SManga>> {
        return if (page==1) flowOf(sampleComicList) else flowOf(emptyList())
    }
}
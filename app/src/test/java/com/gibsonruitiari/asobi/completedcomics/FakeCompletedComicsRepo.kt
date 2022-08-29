package com.gibsonruitiari.asobi.completedcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCompletedComicsRepo:CompletedComicsRepo {
    override fun getCompletedComics(page: Int): Flow<List<SManga>> {
        return if (page==1) flowOf(sampleComicList) else flowOf(emptyList())
    }
}
package com.gibsonruitiari.asobi.latestcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLatestComicsRepo:LatestComicsRepo {
    // stimulates end of page scenario, in real life empty mangas ought to be return to signal end of page
    override fun getLatestComics(page: Int): Flow<List<SManga>> {
        return if (page==1) flowOf(sampleComicList) else flowOf(emptyList())
    }
}
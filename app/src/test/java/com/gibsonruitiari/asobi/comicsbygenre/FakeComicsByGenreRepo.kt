package com.gibsonruitiari.asobi.comicsbygenre

import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeComicsByGenreRepo:ComicsByGenreRepo {
    override fun getComicsByGenre(page: Int, genre: Genres): Flow<List<SManga>> {
        return if (page==1) flowOf(sampleComicList) else flowOf(emptyList())
    }
}
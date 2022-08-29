package com.gibsonruitiari.asobi.comicsbysearch

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.searchcomics.SearchComicsRepo
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeComicsBySearchRepository:SearchComicsRepo {
    override fun searchForComicWhenGivenASearchTerm(searchTerm: String): Flow<List<SManga>> {
        val searchedManga=sampleComicList.find { it.comicName.contentEquals(searchTerm) }
        return if (searchedManga==null) flowOf(emptyList()) else flowOf(listOf(searchedManga))

    }
}
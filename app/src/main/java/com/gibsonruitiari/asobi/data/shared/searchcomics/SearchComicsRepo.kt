package com.gibsonruitiari.asobi.data.shared.searchcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface SearchComicsRepo {
    fun searchForComicWhenGivenASearchTerm(searchTerm:String):Flow<List<SManga>>
}
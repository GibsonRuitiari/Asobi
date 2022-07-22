package com.gibsonruitiari.asobi.data.shared.searchcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.search
import kotlinx.coroutines.flow.Flow

class SearchComicsDelegate:SearchComicsRepo {
    override fun searchForComicWhenGivenASearchTerm(searchTerm: String): Flow<List<SManga>> =  search(searchTerm)
}
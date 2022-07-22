package com.gibsonruitiari.asobi.domain.searchcomics

import com.gibsonruitiari.asobi.data.shared.searchcomics.SearchComicsRepo
import com.gibsonruitiari.asobi.domain.FlowUseCase

class SearchComicsUseCase constructor(private val searchComicsRepo: SearchComicsRepo) {

    data class SearchComicsUseCaseParams(val searchTerm:String)
}
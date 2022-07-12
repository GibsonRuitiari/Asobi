package com.gibsonruitiari.asobi.data.shared.completedcomics

import com.gibsonruitiari.asobi.data.completedComics
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class CompletedComicsRepoImpl: CompletedComicsRepo {
    override  fun getCompletedComics(page: Int): Flow<List<SManga>> = flow{
        emit(completedComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
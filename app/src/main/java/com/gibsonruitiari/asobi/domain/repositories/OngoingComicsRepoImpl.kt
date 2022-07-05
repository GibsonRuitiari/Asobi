package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.ongoingComics
import com.gibsonruitiari.asobi.data.repositories.OngoingComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class OngoingComicsRepoImpl:OngoingComicsRepo {
    override  fun getOngoingComics(page: Int): Flow<List<SManga>> = flow{
        emit(ongoingComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
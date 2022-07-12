package com.gibsonruitiari.asobi.data.shared.ongoingcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.ongoingComics
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class OngoingComicsRepoImpl: OngoingComicsRepo {
    override  fun getOngoingComics(page: Int): Flow<List<SManga>> = flow{
        emit(ongoingComics(page).firstOrNull()?.mangas ?: emptyList())
    }
}
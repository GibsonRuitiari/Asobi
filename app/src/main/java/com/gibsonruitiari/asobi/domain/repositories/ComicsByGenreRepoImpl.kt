package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.comicsByGenre
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.ComicsByGenreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class ComicsByGenreRepoImpl:ComicsByGenreRepo {
    override  fun getComicsByGenre(page: Int, genre: Genres): Flow<List<SManga>> = flow {
       emit(comicsByGenre(page,genre).firstOrNull()?.mangas ?: emptyList())
    }
}
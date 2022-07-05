package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.comicsByGenre
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.ComicsByGenreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class ComicsByGenreRepoImpl:ComicsByGenreRepo {
    override suspend fun getComicsByGenre(page: Int, genre: Genres): Flow<List<SManga>> {
        return flowOf(comicsByGenre(page,genre).firstOrNull()?.mangas ?: emptyList())
    }
}
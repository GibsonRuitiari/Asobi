package com.gibsonruitiari.asobi.domain.pagingdatasource

import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.comicsByGenre
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.ComicsByGenreRepo
import com.gibsonruitiari.asobi.domain.BaseDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull

class ComicsByGenreDataSource (private val logger: Logger,
private val comicsByGenreRepo: ComicsByGenreRepo):BaseDataSource<SManga>(logger){
    private val genre= MutableStateFlow(Genres.DC_COMICS)
    fun setGenre(genres: Genres){
        /*if the genres is equal to genre.value then it won't change hence saving us from re-fetching data unnecessarily */
        genre.value = genres
        logger.i("new genre set ${genre.value}")
    }
    override suspend fun loadData(page: Int): List<SManga> {
      return comicsByGenreRepo.getComicsByGenre(page,genre.value).firstOrNull() ?: emptyList()
    }
}
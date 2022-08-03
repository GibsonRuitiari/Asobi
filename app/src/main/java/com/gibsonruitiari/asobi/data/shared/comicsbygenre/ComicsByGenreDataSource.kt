package com.gibsonruitiari.asobi.data.shared.comicsbygenre

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.BaseDataSource
import kotlinx.coroutines.flow.*

class ComicsByGenreDataSource (private val logger: Logger,
private val comicsByGenreRepo: ComicsByGenreRepo
):  BaseDataSource<SManga>(logger){
        private var _genre:Genres?=null

    fun setGenre(genres: Genres){
        /*if the genres is equal to genre.value then it won't change hence saving us from re-fetching data unnecessarily */
        if (_genre!=genres){
            _genre=genres
        }
        logger.i("genre set-- data source $_genre")
    }

     override suspend fun loadData(page: Int): List<SManga> {
        logger.i("current genre value $_genre")
        return if (_genre==null) emptyList()
        else comicsByGenreRepo.getComicsByGenre(page, _genre!!).firstOrNull() ?: emptyList()

    }


}

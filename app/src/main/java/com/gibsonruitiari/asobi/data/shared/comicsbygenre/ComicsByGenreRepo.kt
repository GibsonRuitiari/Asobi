package com.gibsonruitiari.asobi.data.shared.comicsbygenre

import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface ComicsByGenreRepo {
     fun getComicsByGenre(page:Int,genre:Genres):Flow<List<SManga>>
}
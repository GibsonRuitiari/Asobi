package com.gibsonruitiari.asobi.data.repositories

import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import kotlinx.coroutines.flow.Flow

interface ComicDetailsRepo {
     fun getComicDetails(comicUrl:String):Flow<SMangaInfo>
}
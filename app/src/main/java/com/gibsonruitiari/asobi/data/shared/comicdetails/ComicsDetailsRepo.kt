package com.gibsonruitiari.asobi.data.shared.comicdetails

import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import kotlinx.coroutines.flow.Flow

interface ComicsDetailsRepo {
      fun getComicDetails(comicUrl:String):Flow<SMangaInfo>
}
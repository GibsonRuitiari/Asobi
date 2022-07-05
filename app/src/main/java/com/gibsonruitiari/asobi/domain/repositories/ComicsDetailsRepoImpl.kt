package com.gibsonruitiari.asobi.domain.repositories

import com.gibsonruitiari.asobi.data.comicDetails
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.data.repositories.ComicsDetailsRepo
import kotlinx.coroutines.flow.Flow

class ComicsDetailsRepoImpl : ComicsDetailsRepo {
    override  fun getComicDetails(comicUrl: String): Flow<SMangaInfo> {
       return comicDetails(comicUrl)
    }
}
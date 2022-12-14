package com.gibsonruitiari.asobi.data.shared.comicsbychapter

import com.gibsonruitiari.asobi.data.datamodels.SMangaChapter
import kotlinx.coroutines.flow.Flow

interface ComicsChapterRepo {
    fun getComicsChapter(url:String):Flow<SMangaChapter>
}
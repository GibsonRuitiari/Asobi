package com.gibsonruitiari.asobi.data.shared.comicsbychapter

import com.gibsonruitiari.asobi.data.comicPages
import com.gibsonruitiari.asobi.data.datamodels.SMangaChapter
import com.gibsonruitiari.asobi.data.shared.comicsbychapter.ComicsChapterRepo
import kotlinx.coroutines.flow.Flow

class ComicsChapterRepoImpl: ComicsChapterRepo {
    override fun getComicsChapter(url: String): Flow<SMangaChapter> {
      return  comicPages(url)
    }
}
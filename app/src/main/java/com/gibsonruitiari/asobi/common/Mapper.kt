package com.gibsonruitiari.asobi.common

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.datamodels.SMangaChapter
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.presenter.uiModels.*

interface Mapper<in Input,out Output>{
    operator fun invoke(input: Input):Output
}


val sMangaToViewComicMapper = object :Mapper<SManga,ViewComics>{
    override fun invoke(input: SManga): ViewComics =ViewComics(comicName = input.comicName, comicLink = input.comicLink,
    comicThumbnail = input.comicThumbnailLink)

}
val sMangaDetailsToViewComicDetails = object: Mapper<SMangaInfo,ViewComicDetails>{
    override fun invoke(input: SMangaInfo): ViewComicDetails = ViewComicDetails(comicAlternateName = input.comicAlternateName,
        comicDescription = input.comicDescription, comicAuthor = input.comicAuthor, comicViews = input.comicViews, comicImagePosterLink = input.comicImagePosterLink,
        comicStatus = input.comicStatus,
            similarComics = input.similarManga.map { sManga -> sMangaToViewComicMapper(sManga) },
        comicIssues = input.issues.map { ViewComicIssues(it.issueName,it.issueLink,it.issueReleaseDate) },
        genres = input.genres, yearOfRelease = input.yearOfRelease)

}

val sMangaChapterToViewMangaChapter = object :Mapper<SMangaChapter, ViewComicChapter>{
    override fun invoke(input: SMangaChapter): ViewComicChapter {
        val viewComicPages=input.pages.map { ViewComicPage(pageDetail = it.pageDetail, pageThumbnail = it.pageThumbnail) }
       return ViewComicChapter(totalPages = input.totalPages, comicPages = viewComicPages)
    }
}
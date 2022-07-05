package com.gibsonruitiari.asobi.common

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComicDetails
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComicIssues
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics

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
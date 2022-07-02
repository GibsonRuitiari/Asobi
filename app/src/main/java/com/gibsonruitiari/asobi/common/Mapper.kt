package com.gibsonruitiari.asobi.common

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics

interface Mapper<in Input,out Output>{
    fun map(input: Input):Output
}


val sMangaToViewComicMapper = object :Mapper<SManga,ViewComics>{
    override fun map(input: SManga): ViewComics = ViewComics(comicName = input.comicName, comicLink = input.comicLink,
        comicThumbnail = input.comicThumbnailLink)
}
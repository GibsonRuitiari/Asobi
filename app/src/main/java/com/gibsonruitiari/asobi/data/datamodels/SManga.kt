package com.gibsonruitiari.asobi.data.datamodels

interface SManga {
    var comicName:String
    var comicThumbnailLink:String
    var comicLink:String
    var latestIssue:String? // might be empty most of the times
    companion object{
        fun create():SManga{
            return SMangaImpl()
        }
    }


}
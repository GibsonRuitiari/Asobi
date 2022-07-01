package com.gibsonruitiari.asobi.data.datamodels

interface SMangaInfo {
    var comicAlternateName:String
    var comicDescription:String
    var comicImagePosterLink:String
    var genres:List<String>
    var comicAuthor:String
    var comicViews:Double
    var comicStatus:String // ongoing or completed
    var yearOfRelease:String
    var issues:List<SMangaIssue>
    // comic similar to this manga
    var similarManga:List<SManga> // they may be empty though
    companion object{
        fun create():SMangaInfo{
            return SMangaInfoImpl()
        }
    }


}
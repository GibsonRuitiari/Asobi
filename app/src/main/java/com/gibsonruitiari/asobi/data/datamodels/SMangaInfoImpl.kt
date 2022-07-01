package com.gibsonruitiari.asobi.data.datamodels

class SMangaInfoImpl : SMangaInfo {
    override  var comicAlternateName: String =""

    override  var comicDescription: String= ""

    override  var comicImagePosterLink: String =""
    override  var genres: List<String> = emptyList()

    override  var comicAuthor: String=""

    override  var comicViews: Double =0.0

    override  var comicStatus: String =""

    override  var yearOfRelease: String =""

    override  var issues: List<SMangaIssue> = emptyList()

    override  var similarManga: List<SManga> = emptyList()


}

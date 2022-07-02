package com.gibsonruitiari.asobi.presenter.uiModels

data class ViewComicDetails(val comicAlternateName:String,
                            val comicDescription:String,
                            val comicImagePosterLink:String,
                            val genres:List<String>,
                            val comicAuthor:String,
                            val comicViews:Double,
                            val comicStatus:String, // ongoing or completed,
                            val yearOfRelease:String,
                            val comicIssues:List<ViewComicIssues>,val similarComics:List<ViewComics>)
package com.gibsonruitiari.asobi.data

import com.gibsonruitiari.asobi.data.comicsource.ComicSource
import com.gibsonruitiari.asobi.data.comicsource.ComicSourceImpl
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.MangaPage
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.datamodels.SMangaChapter
import kotlinx.coroutines.flow.Flow


/* Lazily Provide [ComicSourceImpl] instance to be used by clients nb:use of this is optional but preferred*/
private object ComicApiEntryPoint{
    operator fun invoke():ComicSourceImpl{
        val comicsSourceInstance by lazy { ComicSourceImpl() }
        return comicsSourceInstance
    }
}

/** An abstract implementation that can be used to interact with [ComicSource]'s methods/functions such as
  [ComicSource.fetchLatestComics(page:Int)] that require page as their parameter
 **/
private object ComicsSourceWithNumberAsParam{
    operator fun invoke(page:Int, block: ComicSource.(page:Int)->Flow<MangaPage>):Flow<MangaPage>{
        val comicSourceInstance = ComicApiEntryPoint()
        return comicSourceInstance.block(page)
    }
}
/**
 * Fetch manga pages when given an url mostly this abstraction is to be used by comic details and comic pages functions that
 * are provided by [ComicSource]
 */
private object ComicsWithUrl{
    operator fun<T> invoke(url:String,block:ComicSource.(String)->Flow<T>):Flow<T>{
        val comicSourceInstance = ComicApiEntryPoint()
        return comicSourceInstance.block(url)
    }
}

// apis to be used/consumed by client
val comicsByGenre:(page:Int,category: Genres)->Flow<MangaPage> = { page:Int, category:Genres-> ComicApiEntryPoint().fetchComicsByGenre(page,category)}
val latestComics:(page:Int)->Flow<MangaPage> = {it->ComicsSourceWithNumberAsParam(it){fetchLatestComics(it)} }
val popularComics:(page:Int)->Flow<MangaPage> = {it-> ComicsSourceWithNumberAsParam(it){fetchPopularComics(it)} }
val ongoingComics:(page:Int)->Flow<MangaPage> = {it->ComicsSourceWithNumberAsParam(it){fetchOnGoingComics(it)} }
val completedComics:(page:Int)->Flow<MangaPage> ={it->ComicsSourceWithNumberAsParam(it){fetchCompletedComics(it)} }
val comicDetails:(url:String)->Flow<SMangaInfo> = { it-> ComicsWithUrl(it){fetchMangaDetails(it)} }
val comicPages:(url:String) -> Flow<SMangaChapter> = { it-> ComicsWithUrl(it){fetchComicPages(it)}}
val search:(term:String)->Flow<List<SManga>> = {it-> ComicsWithUrl(it){searchForComicWhenGivenASearchTerm(searchTerm = it)} }

package com.gibsonruitiari.asobi.data.shared.latestcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.latestComics
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class LatestComicsRepoImpl: LatestComicsRepo {
    override  fun getLatestComics(page: Int): Flow<List<SManga>> = flow {
         latestComics(page)
             .catch { println("error $it") }
             .collect{
              val data = it.mangas
             doActionIfWeAreOnDebug { data.forEach {   println("comic name --> ${it.comicName} comic link--> ${it.comicLink}") } }
             emit(data)
         }
    }
}
package com.gibsonruitiari.asobi.data.shared.completedcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface CompletedComicsRepo {
     fun getCompletedComics(page:Int):Flow<List<SManga>>
}
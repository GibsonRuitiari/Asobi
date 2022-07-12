package com.gibsonruitiari.asobi.data.shared.ongoingcomics

import com.gibsonruitiari.asobi.data.datamodels.SManga
import kotlinx.coroutines.flow.Flow

interface OngoingComicsRepo {
     fun getOngoingComics(page:Int):Flow<List<SManga>>
}
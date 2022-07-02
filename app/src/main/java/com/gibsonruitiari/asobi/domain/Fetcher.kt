package com.gibsonruitiari.asobi.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.gibsonruitiari.asobi.domain.pagingdatasource.LatestComicsDataSource

class Fetcher(private val source: LatestComicsDataSource) {
 suspend fun x(){
      val pager=Pager(config = PagingConfig(16,12),
      pagingSourceFactory = {source}).flow

  }
}
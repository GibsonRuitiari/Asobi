package com.gibsonruitiari.asobi.viewmodelstest

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.repositories.CompletedComicsRepo
import com.gibsonruitiari.asobi.data.repositories.LatestComicsRepo
import com.gibsonruitiari.asobi.data.repositories.OngoingComicsRepo
import com.gibsonruitiari.asobi.data.repositories.PopularComicsRepo
import com.gibsonruitiari.asobi.domain.interactor.observers.DiscoverComicsUseCase
import com.gibsonruitiari.asobi.dummyComicList
import com.gibsonruitiari.asobi.presenter.viewmodels.DiscoverViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

@Test
fun testDiscoverUseCase() {
  val fakeLatestComicsRepoImpl = object :LatestComicsRepo{
      override fun getLatestComics(page: Int): Flow<List<SManga>> {
          return flowOf(dummyComicList)
      }
  }
    val fakeOngoingComicsRepoImpl = object :OngoingComicsRepo{
        override fun getOngoingComics(page: Int): Flow<List<SManga>> {
            return flowOf(dummyComicList)
        }

    }
    val fakePopularComicsRepoImpl = object :PopularComicsRepo{
        override fun getPopularComics(page: Int): Flow<List<SManga>> {
            return flowOf(dummyComicList)
        }
    }
    val fakeCompletedComicsRepoImpl = object :CompletedComicsRepo{
        override fun getCompletedComics(page: Int): Flow<List<SManga>> {
            return flowOf(dummyComicList)
        }
    }
    val fakeDiscoverUseCaseInstance = DiscoverComicsUseCase(latestComicsRepo = fakeLatestComicsRepoImpl,
    ongoingComicsRepo = fakeOngoingComicsRepoImpl, popularComicsRepo = fakePopularComicsRepoImpl, completedComicsRepo = fakeCompletedComicsRepoImpl)
    val discoverViewModelInstance = DiscoverViewModel(fakeDiscoverUseCaseInstance)
//    discoverViewModelInstance.observeState().runTest{
//
//    }
}
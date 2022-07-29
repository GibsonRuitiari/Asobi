@file:OptIn(ExperimentalCoroutinesApi::class)
package com.gibsonruitiari.asobi.viewmodelstest

import app.cash.turbine.test
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
import com.gibsonruitiari.asobi.domain.DiscoverComicsUseCase
import com.gibsonruitiari.asobi.dummyComicList
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverComicsResult
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class ViewModelTests {

    private var fakeDiscoverUseCaseInstance: DiscoverComicsUseCase?=null
    @Before
    fun initializeFakeDiscoverUseCaseInstance(){
//        val fakeLatestComicsRepoImpl = object : LatestComicsRepo {
//            override fun getLatestComics(page: Int): Flow<List<SManga>> {
//                return flowOf(dummyComicList)
//            }
//        }
//        val fakeOngoingComicsRepoImpl = object : OngoingComicsRepo {
//            override fun getOngoingComics(page: Int): Flow<List<SManga>> {
//                return flowOf(dummyComicList)
//            }
//
//        }
//        val fakePopularComicsRepoImpl = object : PopularComicsRepo {
//            override fun getPopularComics(page: Int): Flow<List<SManga>> {
//                return flowOf(dummyComicList)
//            }
//        }
//        val fakeCompletedComicsRepoImpl = object : CompletedComicsRepo {
//            override fun getCompletedComics(page: Int): Flow<List<SManga>> {
//                return flowOf(dummyComicList)
//            }
//        }
//         fakeDiscoverUseCaseInstance = DiscoverComicsUseCase(
//            latestComicsRepo = fakeLatestComicsRepoImpl,
//            ongoingComicsRepo = fakeOngoingComicsRepoImpl,
//            popularComicsRepo = fakePopularComicsRepoImpl,
//            completedComicsRepo = fakeCompletedComicsRepoImpl, genreComicsRepo = fakeCompletedComicsRepoImpl)
    }
//    @Test
//    fun `assert That DiscoverComicsUseCase Respects the Params It is Given`() = runTest {
//        fakeDiscoverUseCaseInstance?.run(DiscoverComicsUseCase.DiscoverComicsParams(2,Genres.DC_COMICS))?.test {
//
//            println(expectMostRecentItem().completedComics.comicsData.size)
//          //  Assert.assertEquals(2, expectMostRecentItem().completedComics.comicsData.size)
//
//        }
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assertDiscoverViewModel's State Is Not Empty Whenever ViewModel Instance is Created`():Unit = runTest{
        /* MainDispatcher is unavailable in local tests so replace it */
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            /* do work that concerns view model*/
            val discoverViewModelInstance = DiscoverViewModel(fakeDiscoverUseCaseInstance!!)
            /* since the loadComicsAction is initialized inside the view-model's constructor init{} the state must not be empty */
            assertNotEquals(DiscoverComicsResult.EMPTY, discoverViewModelInstance.observeState().value)
        }finally {
            /* reset dispatcher to use Dispatchers.Main */
            Dispatchers.resetMain()
        }
    }
    @After
    fun tearInitializations(){
        // nullify the discover use case to make GC garbage collect it
        fakeDiscoverUseCaseInstance=null
    }
}

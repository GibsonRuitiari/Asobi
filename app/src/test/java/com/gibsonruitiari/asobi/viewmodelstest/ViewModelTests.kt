@file:OptIn(ExperimentalCoroutinesApi::class)
package com.gibsonruitiari.asobi.viewmodelstest

import kotlinx.coroutines.ExperimentalCoroutinesApi

class ViewModelTests {

//    private var fakeDiscoverUseCaseInstance: DiscoverComicsUseCase?=null
//    @Before
//    fun initializeFakeDiscoverUseCaseInstance(){
////        val fakeLatestComicsRepoImpl = object : LatestComicsRepo {
////            override fun getLatestComics(page: Int): Flow<List<SManga>> {
////                return flowOf(dummyComicList)
////            }
////        }
////        val fakeOngoingComicsRepoImpl = object : OngoingComicsRepo {
////            override fun getOngoingComics(page: Int): Flow<List<SManga>> {
////                return flowOf(dummyComicList)
////            }
////
////        }
////        val fakePopularComicsRepoImpl = object : PopularComicsRepo {
////            override fun getPopularComics(page: Int): Flow<List<SManga>> {
////                return flowOf(dummyComicList)
////            }
////        }
////        val fakeCompletedComicsRepoImpl = object : CompletedComicsRepo {
////            override fun getCompletedComics(page: Int): Flow<List<SManga>> {
////                return flowOf(dummyComicList)
////            }
////        }
////         fakeDiscoverUseCaseInstance = DiscoverComicsUseCase(
////            latestComicsRepo = fakeLatestComicsRepoImpl,
////            ongoingComicsRepo = fakeOngoingComicsRepoImpl,
////            popularComicsRepo = fakePopularComicsRepoImpl,
////            completedComicsRepo = fakeCompletedComicsRepoImpl, genreComicsRepo = fakeCompletedComicsRepoImpl)
//    }
////    @Test
////    fun `assert That DiscoverComicsUseCase Respects the Params It is Given`() = runTest {
////        fakeDiscoverUseCaseInstance?.run(DiscoverComicsUseCase.DiscoverComicsParams(2,Genres.DC_COMICS))?.test {
////
////            println(expectMostRecentItem().completedComics.comicsData.size)
////          //  Assert.assertEquals(2, expectMostRecentItem().completedComics.comicsData.size)
////
////        }
////    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `assertDiscoverViewModel's State Is Not Empty Whenever ViewModel Instance is Created`():Unit = runTest{
//        /* MainDispatcher is unavailable in local tests so replace it */
//        val testDispatcher = UnconfinedTestDispatcher()
//        Dispatchers.setMain(testDispatcher)
//
//        try {
//            /* do work that concerns view model*/
//            val discoverViewModelInstance = DiscoverViewModel(fakeDiscoverUseCaseInstance!!)
//            /* since the loadComicsAction is initialized inside the view-model's constructor init{} the state must not be empty */
//            assertNotEquals(DiscoverComicsResult.EMPTY, discoverViewModelInstance.observeState().value)
//        }finally {
//            /* reset dispatcher to use Dispatchers.Main */
//            Dispatchers.resetMain()
//        }
//    }
//    @After
//    fun tearInitializations(){
//        // nullify the discover use case to make GC garbage collect it
//        fakeDiscoverUseCaseInstance=null
//    }
}

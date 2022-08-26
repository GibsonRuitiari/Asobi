package com.gibsonruitiari.asobi.data.popularcomics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsDataSource
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
import com.gibsonruitiari.asobi.domain.popularcomics.PagedPopularComicsObserver
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import com.gibsonruitiari.asobi.ui.popularcomics.PopularComicsViewModel
import com.gibsonruitiari.asobi.utilities.logging.AsobiLogger
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class PaginatedPopularComicsDataSourceTest:KoinTest {
    private lateinit var popularComicsRepository:PopularComicsRepo
    private lateinit var pagedPopularComicsObserver: PagedPopularComicsObserver
    private val pagingConfig = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 30,
        enablePlaceholders = false)
    private lateinit var popularComicsDataSource: PopularComicsDataSource
    private val logger:Logger by inject()
    private lateinit var popularComicsViewModel: PopularComicsViewModel
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @Before
    fun createTestSubjectInstances(){
        startKoin {
            modules(
                module { single<Logger> { AsobiLogger() }}
            )
        }
        popularComicsRepository = PopularComicsRepoImplTest()
        popularComicsDataSource=PopularComicsDataSource(logger,popularComicsRepository)
        pagedPopularComicsObserver= PagedPopularComicsObserver(logger,popularComicsRepository)
        popularComicsViewModel = PopularComicsViewModel(pagedPopularComicsObserver)
    }

    @Test
    fun `logger component is not null`(){
        assertNotNull(logger)
    }

    @Test
    fun `popular comics data source returns page when load method is called`() = runTest{
        assertEquals(expected = PagingSource.LoadResult.Page(data = sampleComicList,
        prevKey = 0, nextKey= 2),
            actual=popularComicsDataSource.load(PagingSource.LoadParams.Refresh(key = 1,loadSize =5,
                placeholdersEnabled=false)))
    }
    @After
    fun destroyTestSubjectInstances(){
        stopKoin()
    }
}
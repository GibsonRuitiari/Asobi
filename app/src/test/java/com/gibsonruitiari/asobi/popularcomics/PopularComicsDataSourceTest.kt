package com.gibsonruitiari.asobi.popularcomics

import androidx.paging.PagingSource
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsDataSource
import com.gibsonruitiari.asobi.domain.popularcomics.PagedPopularComicsObserver
import com.gibsonruitiari.asobi.testcommon.PagedDataSourceTest
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import com.gibsonruitiari.asobi.ui.popularcomics.PopularComicsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PopularComicsDataSourceTest:PagedDataSourceTest() {
    private lateinit var popularComicsDataSource:PopularComicsDataSource
    private lateinit var popularComicsViewModel:PopularComicsViewModel
    @Before
    fun setUpLatestComicsTestSubjects(){
        val popularComicsRepository = FakePopularComicsRepo()
        popularComicsDataSource = PopularComicsDataSource(logger,popularComicsRepository)
        popularComicsViewModel = PopularComicsViewModel(PagedPopularComicsObserver(logger,
        popularComicsRepository))
    }
    @Test
    fun `popular comics data source's load data method returns correct manga list when given page number`() = runTest{
        performTest {
            val actual = popularComicsDataSource.loadData(defaultPageNumber)
            assertContentEquals(sampleComicList,actual)
        }
    }
    @Test
    fun `load result of type page is returned whenever load method of the data source is called with page number 1`() = runTest {
        performTest {
            val loadResult = popularComicsDataSource.load(loadParam)
            assertTrue(loadResult is PagingSource.LoadResult.Page)
            assertContentEquals(sampleComicList,loadResult.data)
            assertTrue(loadResult.prevKey == null,"previous page number ought to be null when we are at the first page")
        }
    }
    @Test
    fun `paging snapshot data is received and equals the expected data once submit method is invoked() `() = runTest {
        performTest {
            popularComicsViewModel.pagedList.collect{dataDiffer.submitData(it)}
            val popularComicsSnapshot = dataDiffer.snapshot().items
            assertTrue(popularComicsSnapshot.isNotEmpty())
            assertEquals(sampleComicList.size, popularComicsSnapshot.size)

            assertTrue(popularComicsSnapshot[0].comicName.contentEquals("The Boys",ignoreCase = true))
            assertTrue(popularComicsSnapshot[1].comicName.contentEquals("The Guardians of the Galaxy",ignoreCase = true))
        }
    }
    @Test
    fun `load result is of type page but data is empty when load method of data source is called with page number ==2`() = runTest {
        performTest {
            keyNumber=2
            loadParam = PagingSource.LoadParams.Append(key=keyNumber,placeholdersEnabled = false, loadSize = 0)
            val loadResult = popularComicsDataSource.load(loadParam)
            assertTrue(loadResult is PagingSource.LoadResult.Page)
            assertTrue(loadResult.data.isEmpty())
            assertTrue(loadResult.prevKey !=null && loadResult.prevKey==1)
        }

    }
}

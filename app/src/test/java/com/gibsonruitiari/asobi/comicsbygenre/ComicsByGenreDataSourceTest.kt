package com.gibsonruitiari.asobi.comicsbygenre

import androidx.paging.PagingSource
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreDataSource
import com.gibsonruitiari.asobi.domain.bygenre.PagedComicsByGenreObserver
import com.gibsonruitiari.asobi.testcommon.PagedDataSourceTest
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModel
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModelDelegate
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ComicsByGenreDataSourceTest:PagedDataSourceTest() {
    private lateinit var comicsByGenreDataSource: ComicsByGenreDataSource
    private lateinit var comicsByGenreViewModel:ComicsByGenreViewModel
    @Before
    fun setUpLatestComicsTesSubjects(){
        val comicsByGenreFilterViewModel:ComicFilterViewModel = ComicFilterViewModelDelegate()
        val comicsByGenreRepository = FakeComicsByGenreRepo()
        comicsByGenreDataSource=ComicsByGenreDataSource(logger,comicsByGenreRepository)
        comicsByGenreViewModel=ComicsByGenreViewModel(PagedComicsByGenreObserver(logger,comicsByGenreRepository),comicsByGenreFilterViewModel)
    }
    @Test
    fun `comics by genre data source's load data method returns correct manga list when given page number`()= runTest{
        performTest {
            val actual=comicsByGenreDataSource.loadData(defaultPageNumber)
            assertContentEquals(sampleComicList,actual)
        }
    }
    @Test
    fun `load result of type page is returned whenever load method of the data source is called with page number 1`() = runTest {
        performTest {
            val loadResult=comicsByGenreDataSource.load(loadParam)
            assertTrue(loadResult is PagingSource.LoadResult.Page)
            assertContentEquals(sampleComicList,loadResult.data)
            assertTrue(loadResult.prevKey == null,"previous page number ought to be null when we are at the first page")
        }
    }
    @Test
    fun `load param key updates accordingly when given a different key number other than the initial`()= runTest {
        keyNumber =2
        // items to be added at the end of the list
        // load size is 0 since we have reached the end of the data source
        loadParam = PagingSource.LoadParams.Append(key = keyNumber, placeholdersEnabled = false, loadSize = 0)
        assertTrue(loadParam.key==2)
    }

    @Test
    fun `paging snapshot data is received and equals the expected data once submit method is invoked() `() = runTest {
        performTest {
            comicsByGenreViewModel
                .comicsList
                .collect{dataDiffer.submitData(it)}
            val latestComicsSnapshot = dataDiffer.snapshot().items
            assertTrue(latestComicsSnapshot.isNotEmpty())
            assertEquals(sampleComicList.size, latestComicsSnapshot.size)

            assertTrue(latestComicsSnapshot[0].comicName.contentEquals("The Boys",ignoreCase = true))
            assertTrue(latestComicsSnapshot[1].comicName.contentEquals("The Guardians of the Galaxy",ignoreCase = true))
        }
    }

    @Test
    fun `load result is of type page but data is empty when load method of data source is called with page number ==2`() = runTest {
        performTest {
            keyNumber=2
            loadParam = PagingSource.LoadParams.Append(key = keyNumber, placeholdersEnabled = false, loadSize = 0)
            val loadResult=comicsByGenreDataSource.load(loadParam)
            assertTrue(loadResult is PagingSource.LoadResult.Page)
            assertTrue(loadResult.data.isEmpty())
            assertTrue(loadResult.prevKey !=null && loadResult.prevKey==1)
        }
    }
    @Test
    fun `comics by genre data source returns empty list when data source reaches the end of the list`() = runTest {
        performTest {
            val actual = comicsByGenreDataSource.loadData(noDataPageNumber)
            assertTrue(actual.isEmpty(),"latest comics snapshot ought to be empty since there is no more data to be loaded!Check your implementation")
        }
    }
}
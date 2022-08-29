package com.gibsonruitiari.asobi.ongoingcomics

import com.gibsonruitiari.asobi.testcommon.getOrThrow
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PopularComicsRepositoryTest {
    private lateinit var ongoingComicsRepo: com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
    @Before
    fun setUpPopularComicsRepoSubject(){
        ongoingComicsRepo = FakeOngoingComicsRepo()
    }
    @Test
    fun `test ongoing ComicsList Content Equals the SampleDataSource Content`() = runTest {
        performTest {
            val popularComicsList= ongoingComicsRepo.getOngoingComics(1).getOrThrow()
            assertContentEquals(expected = sampleComicList, actual = popularComicsList)
        }
    }
    @Test
    fun `test ongoing ComicsList First Comic Equals SampleDataSource First Comic`() = runTest{
        performTest {
            val popularComicList = ongoingComicsRepo.getOngoingComics(1).getOrThrow("expected ongoing comics to be emitted but non was found;check repo implementation")
            val expected = popularComicList[0]
            val actual = sampleComicList[0]
            assertEquals(expected,actual)
        }
    }


}
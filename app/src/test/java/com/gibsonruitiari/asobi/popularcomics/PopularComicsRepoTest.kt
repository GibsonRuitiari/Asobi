package com.gibsonruitiari.asobi.popularcomics

import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo
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
class PopularComicsRepoTest {
    private lateinit var popularComicsRepo: PopularComicsRepo
    @Before
    fun setUpPopularComicsRepoSubject(){
     popularComicsRepo = FakePopularComicsRepo()
    }
    @Test
    fun `test popularComicsList Content Equals the SampleDataSource Content`() = runTest {
        performTest {
            val popularComicsList= popularComicsRepo.getPopularComics(1).getOrThrow()
            assertContentEquals(expected = sampleComicList, actual = popularComicsList)
        }
    }
    @Test
    fun `test popularComicsList First Comic Equals SampleDataSource First Comic`() = runTest{
        performTest {
            val popularComicList = popularComicsRepo.getPopularComics(1).getOrThrow("expected popular comics to be emitted but non was found;check repo implementation")
            val expected = popularComicList[0]
            val actual = sampleComicList[0]
            assertEquals(expected,actual)
        }
    }


}
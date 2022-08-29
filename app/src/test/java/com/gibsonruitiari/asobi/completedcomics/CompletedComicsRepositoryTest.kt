package com.gibsonruitiari.asobi.completedcomics

import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
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
class CompletedComicsRepositoryTest {
    private lateinit var completedComicsRepo: CompletedComicsRepo
    @Before
    fun setUpPopularComicsRepoSubject(){
        completedComicsRepo = FakeCompletedComicsRepo()
    }
    @Test
    fun `test completed ComicsList Content Equals the SampleDataSource Content`() = runTest {
        performTest {
            val completedComicsList= completedComicsRepo.getCompletedComics(1).getOrThrow()
            assertContentEquals(expected = sampleComicList, actual = completedComicsList)
        }
    }
    @Test
    fun `test completed ComicsList First Comic Equals SampleDataSource First Comic`() = runTest{
        performTest {
            val completedComicList = completedComicsRepo.getCompletedComics(1).
            getOrThrow("expected completed comics to be emitted but non was found;check repo implementation")
            val expected = completedComicList[0]
            val actual = sampleComicList[0]
            assertEquals(expected,actual)
        }
    }

}
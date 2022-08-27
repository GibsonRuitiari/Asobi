package com.gibsonruitiari.asobi.latestcomics

import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.testcommon.getOrThrow
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LatestComicsRepositoryTest {
    private val defaultPageNumber =1
    private val nOpPageNumber =2
    private lateinit var latestComicsRepo:LatestComicsRepo
    @Before
    fun setUpTestSubject(){
        latestComicsRepo = FakeLatestComicsRepo()
    }
    @Test
    fun `latest comics list returned is similar to sampleComics data`() = runTest {
        performTest {
            val latestComics=latestComicsRepo.getLatestComics(defaultPageNumber).getOrThrow("latest comics should not be null;check the existing implementation of latest comics repository")
            assertContentEquals(sampleComicList,latestComics)
        }
    }
    @Test
    fun `when in the last page should return an empty list of mangas`() = runTest{
        performTest {
            val latestComicsRepo=latestComicsRepo.getLatestComics(nOpPageNumber).getOrThrow("latest comics should not be null;check the existing implementation of latest comics repository")
            assertTrue(latestComicsRepo.isEmpty(),"end of page reached, latest comics data ought to be empty")
        }
    }
}
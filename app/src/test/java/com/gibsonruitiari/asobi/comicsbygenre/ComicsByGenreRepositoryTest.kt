package com.gibsonruitiari.asobi.comicsbygenre

import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreRepo
import com.gibsonruitiari.asobi.testcommon.getOrThrow
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ComicsByGenreRepositoryTest {
    private val defaultPageNumber =1
    private val nOpPageNumber =2
    private lateinit var comicsByGenreRepo:ComicsByGenreRepo
    @Before
    fun setUpTestSubject(){
        comicsByGenreRepo = FakeComicsByGenreRepo()
    }
    @Test
    fun `comics by genre list returned is similar to sampleComics data`() = runTest {
        performTest {
            val comicsByGenre=comicsByGenreRepo.getComicsByGenre(defaultPageNumber,Genres.DC_COMICS).getOrThrow("comics by genre should not be null;check the existing implementation of comics by genre repository")
            assertContentEquals(sampleComicList,comicsByGenre)
        }
    }
    @Test
    fun `when in the last page should return an empty list of mangas`() = runTest{
        performTest {
            val comicsByGenre=comicsByGenreRepo.getComicsByGenre(nOpPageNumber,Genres.DC_COMICS).getOrThrow("comics by genre should not be null;check the existing implementation of comics by genre repository")
            assertTrue(comicsByGenre.isEmpty(),"end of page reached, comics by genre data ought to be empty")
        }
    }

}
package com.gibsonruitiari.asobi.comicsbygenre

import app.cash.turbine.test
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModel
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModelDelegate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ComicFilterViewModelTest {
    private lateinit var comicFilterViewModel: ComicFilterViewModel
    private val defaultGenre = Genres.DC_COMICS
    private val selectedGenre = Genres.MARVEL
    @Before
    fun setUpTestSubjectInstance(){
        comicFilterViewModel = ComicFilterViewModelDelegate()

    }
    @Test
    fun `genre is set when setGenre() method is called`() = runTest {
        performTest {
            comicFilterViewModel.setGenre(selectedGenre)
            comicFilterViewModel.currentGenreChoice.test {
               val actual= expectMostRecentItem()
               assertEquals(expected=selectedGenre,actual)
            }
        }
    }
    @Test
    fun `when reset Genre Choice method is invoked selected genre is DC_COMICS`() = runTest {
        performTest {
            comicFilterViewModel.resetGenreChoice()
            comicFilterViewModel.currentGenreChoice.test {
                val genreChoice =expectMostRecentItem()
                assertNotNull(genreChoice,"comic genre choice not reset;check your implementation")
                assertEquals(defaultGenre,genreChoice)
            }
        }
    }
}
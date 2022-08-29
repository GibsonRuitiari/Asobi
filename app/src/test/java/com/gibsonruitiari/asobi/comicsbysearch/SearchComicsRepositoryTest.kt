package com.gibsonruitiari.asobi.comicsbysearch

import app.cash.turbine.test
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.shared.searchcomics.SearchComicsRepo
import com.gibsonruitiari.asobi.testcommon.performTest
import com.gibsonruitiari.asobi.testcommon.sampleComicList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchComicsRepositoryTest {
  private lateinit var searchComicsRepo: SearchComicsRepo
  private var searchTerm ="The Boys"
  private val weirdErrorneousSearchTerm ="aahfajajj" // this feels illegal I swear
  @Before
  fun setUpTestSubjects(){
      searchComicsRepo = FakeComicsBySearchRepository()
  }

  @Test
  fun `returns search result when search term is submitted`() = runTest {
      performTest {
          searchComicsRepo.searchForComicWhenGivenASearchTerm(searchTerm).test {
              val actual = expectMostRecentItem()
              assertEquals(expected = sampleComicList.first { it.comicName.contentEquals(searchTerm) }, actual = actual[0])
          }
      }
  }
    @Test
    fun `returns empty search result when a comic not in the dataset is submitted for search`() = runTest {
        performTest {
            searchComicsRepo.searchForComicWhenGivenASearchTerm(weirdErrorneousSearchTerm).test {
                assertEquals(emptyList(),awaitItem())
                awaitComplete() // if you don't await completion of consumption of the events an assertion error will be thrown
            }
        }
    }

}
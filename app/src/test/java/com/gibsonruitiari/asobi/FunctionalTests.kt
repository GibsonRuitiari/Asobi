package com.gibsonruitiari.asobi

import app.cash.turbine.test
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import com.gibsonruitiari.asobi.utilities.utils.toNetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FunctionalTests {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test That the toNetworkResourceFunction Maps Correctly `() = runTest{
        val viewComicsList=dummyComicList.map { sManga ->
            sMangaToViewComicMapper(sManga)
        }
        viewComicsList.asFlow().toNetworkResource().test {
            val statusOfRecentItem = expectMostRecentItem().status
            assert(statusOfRecentItem == Status.SUCCESS)
        }
    }
}

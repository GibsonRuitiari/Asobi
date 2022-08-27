package com.gibsonruitiari.asobi.testcommon

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.logging.AsobiLogger
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertNotNull
@OptIn(ExperimentalCoroutinesApi::class)
open class PagedDataSourceTest:KoinTest{
    private val pagedDataDiffUtil = object :DiffUtil.ItemCallback<ViewComics>(){
        override fun areContentsTheSame(oldItem: ViewComics, newItem: ViewComics): Boolean {
            return oldItem==newItem
        }
        override fun areItemsTheSame(oldItem: ViewComics, newItem: ViewComics): Boolean {
            return oldItem.comicName==newItem.comicName
        }
    }
    private val noOpListCallback =object: ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }
    // use Refresh to stimulate initial load
    open lateinit var loadParam:PagingSource.LoadParams<Int>
    open var keyNumber=1
    open val logger: Logger by inject()
    // move from a background executor to a more deterministic executor for arch components
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()
    val dataDiffer = AsyncPagingDataDiffer(diffCallback = pagedDataDiffUtil,
    noOpListCallback, mainDispatcher = mainDispatcherRule.dispatcher)
    @Before
    fun setUpTestSubjects(){
        startKoin { modules(module{single<Logger> { AsobiLogger()}}) }
        loadParam = PagingSource.LoadParams.Refresh(key = keyNumber, loadSize = 7,placeholdersEnabled = false)
    }
    @Test
    fun `assert that logger component is not null and it is being injected properly`(){
        assertNotNull(logger,"ensure koin is started, and the logger module is being loaded properly into the definitions graph")
    }
    @After
    fun tearDownTestSubjects(){
        stopKoin()
    }

}
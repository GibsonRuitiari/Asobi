package com.gibsonruitiari.asobi.presenter.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.common.ScreenSize
import com.gibsonruitiari.asobi.common.extensions.gridLayoutManager
import com.gibsonruitiari.asobi.common.extensions.scrollToTop
import com.gibsonruitiari.asobi.common.extensions.showSnackBar
import com.gibsonruitiari.asobi.common.utils.RecyclerViewItemDecoration
import com.gibsonruitiari.asobi.common.utils.convertToPxFromDp
import com.gibsonruitiari.asobi.databinding.BaseFragmentBinding
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.composedPagedAdapter
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.BindingViewHolder
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.viewHolderFrom
import com.gibsonruitiari.asobi.presenter.uiModels.UiMeasureSpec
import com.gibsonruitiari.asobi.presenter.viewmodels.MainActivityViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseFragment<Item:Any, VH:RecyclerView.ViewHolder>:Fragment(){
    private var _baseFragmentBinding: BaseFragmentBinding?=null
     val fragmentBinding get() = _baseFragmentBinding!!


    abstract val pagingListAdapter:PagingDataAdapter<Item,VH>

    abstract val toolbarTitle:String
    abstract val pagedList:Flow<PagingData<Item>>
    private val activityMainViewModel:MainActivityViewModel by viewModel()

    companion object{
        private  const val  defaultNumberOfColumns =2
        private const val defaultSpacing = 4
        private const val mediumNumberOfColumns = 4
        private const val mediumSpacing = 8
        private const val extendedNumberOfColumns = 6
        private const val extendedSpacing = 12
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _baseFragmentBinding = BaseFragmentBinding.inflate(inflater,container,false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBaseFragmentUiComponents()
        listenToUiStateAndUpdateUiAccordingly()

        viewLifecycleOwner.lifecycleScope.launch {
            println("started collecting")
            /*Perform collection of multiple flows here  */
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch { /*Observe uiMeasureSpec state data */ observeScreenMeasureSpecState() }
                launch {  /* Observe paged data */ observePagedData() }
                launch { /*Observe screen size data */observeScreenWidthState() }
            }
        }
    }
    private suspend fun observePagedData(){
        pagedList.collectLatest {
            println("collecting paging data $it")
            pagingListAdapter.submitData(it)
          //  submitPagingDataToPagingAdapter(it)
        }
    }
    private suspend fun observeScreenWidthState(){
        activityMainViewModel.screenWidthState.collectLatest {screenSize->
            println("screen size $screenSize")
            activityMainViewModel.setUiMeasureSpec(constructUiMeasureSpecFromScreenSize(screenSize))
        }
    }
    private suspend fun observeScreenMeasureSpecState(){
        activityMainViewModel.uiMeasureSpecState.collectLatest {
            setUpBaseFragmentRecyclerView(it)
        }
    }

    private fun listenToUiStateAndUpdateUiAccordingly(){
        pagingListAdapter.addLoadStateListener {
            when(it.refresh){
                is LoadState.NotLoading->{
                    if (pagingListAdapter.itemCount==0){
                        println("empty")
                        showEmptyState()
                        setEmptyStateText(getString(R.string.error_state_title),getString(R.string.empty_title))
                    }
                    else {
                        println("success")
                        showSuccessState()
                    }
                }
                is LoadState.Loading-> {
                    println("loading")
                    showLoadingState()
                }
                is LoadState.Error->{
                    val errorMessage=when((it.refresh as LoadState.Error).error){
                        is UnknownHostException, is SocketTimeoutException, is ConnectException ->{
                           getString(R.string.network_error_msg)
                        }
                        else-> "load state is error: ${ (it.refresh as LoadState.Error).error.message}"
                    }
                    println(errorMessage)
                    fragmentBinding.swipeRefreshLayoutContainer.showSnackBar(errorMessage)
                    fragmentBinding.errorStateLayout.emptyErrorStateTitle.text = getString(R.string.error_state_title)
                    fragmentBinding.errorStateLayout.emptyErrorStateSubtitle.text = errorMessage
                    showErrorState()
                }
            }

            setUpSwipeRefreshWidgetState(fragmentBinding.baseFragSwipeRefresh.isRefreshing && it.refresh is LoadState.Loading)
        }
    }
    private fun setUpSwipeRefreshWidgetState(isRefreshing:Boolean){
        fragmentBinding.baseFragSwipeRefresh.isRefreshing=isRefreshing
    }

    private fun setEmptyStateText(title: String, subtitle: String) {
        fragmentBinding.emptyStateLayout.emptyErrorStateTitle.text = title
        fragmentBinding.emptyStateLayout.emptyErrorStateSubtitle.text = subtitle
    }

    private fun constructUiMeasureSpecFromScreenSize(screenSize: ScreenSize)=when(screenSize){
        ScreenSize.COMPACT->{
            /* layout grid uses 4 columns so for the recycler view's grid layout we need 2 columns */
            /* layout grid uses 16.dp gutter for 4 columns so for the recycler view's grid layout spacing in between columns ought to be 4.dp */
            UiMeasureSpec(recyclerViewColumns =defaultNumberOfColumns, recyclerViewMargin =defaultSpacing)
        }
        ScreenSize.MEDIUM->{
            /* layout grid uses 8 columns so for the recycler view's grid layout we need 4 columns */
            /* layout grid uses 24.dp gutter for 8 columns so for the recycler view's grid layout spacing in between columns ought to be 8.dp */
            UiMeasureSpec(recyclerViewColumns = mediumNumberOfColumns, recyclerViewMargin = mediumSpacing)
        }
        ScreenSize.EXPANDED->{
            /* layout grid uses 12 columns so for the recycler view's grid layout we need 6 columns */
            UiMeasureSpec(recyclerViewColumns = extendedNumberOfColumns, recyclerViewMargin = extendedSpacing)
        }
    }
    private fun showSuccessState() {
        with(fragmentBinding){
            baseFragRecyclerView.isVisible = true
            errorStateLayout.root.isVisible = false
            emptyStateLayout.root.isVisible = false
            contentLoadingLayout.hide()
        }

    }

    private fun showErrorState() {
        with(fragmentBinding){
            baseFragRecyclerView.isVisible = false
            errorStateLayout.root.isVisible = true
            emptyStateLayout.root.isVisible = false
            contentLoadingLayout.hide()
        }

    }

    private fun showEmptyState() {
        with(fragmentBinding){
            baseFragRecyclerView.isVisible = false
            errorStateLayout.root.isVisible = false
            emptyStateLayout.root.isVisible = true
            contentLoadingLayout.hide()
        }

    }

    private fun showLoadingState() {
        with(fragmentBinding){
            baseFragRecyclerView.isVisible = false
            errorStateLayout.root.isVisible = false
            emptyStateLayout.root.isVisible = false
            contentLoadingLayout.show()
        }
    }
    private fun setUpBaseFragmentUiComponents(){
        fragmentBinding.baseFragToolbar.title = toolbarTitle
        fragmentBinding.baseFragSwipeRefresh.setOnRefreshListener { pagingListAdapter.refresh() }

    }
    private fun setUpBaseFragmentRecyclerView(uiMeasureSpec: UiMeasureSpec){
        fragmentBinding.baseFragRecyclerView.apply {
            setHasFixedSize(true)
            scrollToTop()
            adapter = pagingListAdapter
            val spanCount = uiMeasureSpec.recyclerViewColumns
            layoutManager = gridLayoutManager(spanCount = spanCount)
            addItemDecoration(RecyclerViewItemDecoration(spanCount,
                includeEdge = true,
                spacing = requireActivity().convertToPxFromDp(uiMeasureSpec.recyclerViewMargin)))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _baseFragmentBinding = null
    }
}
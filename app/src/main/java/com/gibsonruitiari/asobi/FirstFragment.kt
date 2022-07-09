package com.gibsonruitiari.asobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.gibsonruitiari.asobi.common.ScreenSize
import com.gibsonruitiari.asobi.common.extensions.gridLayoutManager
import com.gibsonruitiari.asobi.common.extensions.scrollToTop
import com.gibsonruitiari.asobi.common.extensions.showSnackBar
import com.gibsonruitiari.asobi.common.utils.RecyclerViewItemDecoration
import com.gibsonruitiari.asobi.common.utils.convertToPxFromDp
import com.gibsonruitiari.asobi.common.utils.loadPhotoUrl
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.databinding.FragmentFirstBinding
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.composedPagedAdapter
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.BindingViewHolder
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.viewHolderDelegate
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.viewHolderFrom
import com.gibsonruitiari.asobi.presenter.uiModels.UiMeasureSpec
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import com.gibsonruitiari.asobi.presenter.viewmodels.MainActivityViewModel
import com.gibsonruitiari.asobi.presenter.viewmodels.PopularComicsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class FirstFragment : Fragment() {

        private var _binding: FragmentFirstBinding? = null

    private val fragmentBinding get() = _binding!!
    private val activityMainViewModel:MainActivityViewModel by viewModel()
    private val popularComicsViewModel:PopularComicsViewModel by viewModel()
    private var pagingListAdapter: PagingDataAdapter<ViewComics, BindingViewHolder<ComicItemLayoutBinding>>? = composedPagedAdapter(createViewHolder = { viewGroup, _ ->
        viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener { onComicClicked(comicItem) }
        }
    },bindViewHolder = { viewHolder: BindingViewHolder<ComicItemLayoutBinding>, item: ViewComics?, _: Int ->
        viewHolder.bind(item)
    })
    private val listAdapter get() = pagingListAdapter!!
 companion object{
     private  const val  defaultNumberOfColumns =2 // by default
     private const val defaultSpacing = 4
     private const val mediumNumberOfColumns = 4
     private const val mediumSpacing = 8
     private const val extendedNumberOfColumns = 6
     private const val extendedSpacing = 12
 }
    private var BindingViewHolder<ComicItemLayoutBinding>.comicItem by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(comics: ViewComics?){
        comics?.let {
            this.comicItem = comics
            binding.comicsImageView.loadPhotoUrl(it.comicThumbnail)
        }
    }


    private fun onComicClicked(comics: ViewComics){
        Toast.makeText(requireContext(),"${comics.comicLink} clicked",Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            /*Perform collection of multiple flows here  */
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {   /*Observe uiMeasureSpec state data */observeUiMeasureSpecState() }
                launch { /* Observe paged data */observePagedData() }
                launch {/*Observe uiMeasureSpec state data */  observeScreenSizeAndSetUiMeasureSpec() }

            }
        }
        listenToUiStateAndUpdateUiAccordingly()
        setUpBasicFragmentsComponents()
    }
    private suspend fun observeUiMeasureSpecState(){
        activityMainViewModel.uiMeasureSpecState.collectLatest {
            initializePagedDataRecyclerView(it)
        }
    }
    private suspend fun observePagedData(){
        popularComicsViewModel.pagedList.collectLatest {
            pagingListAdapter?.submitData(it)
        }
    }
    private suspend fun observeScreenSizeAndSetUiMeasureSpec(){
        activityMainViewModel.screenWidthState.collectLatest {
            val spec=constructUiMeasureSpec(it)
            activityMainViewModel.setUiMeasureSpec(spec)
        }
    }
    private fun listenToUiStateAndUpdateUiAccordingly(){
        pagingListAdapter?.addLoadStateListener {
            when(it.refresh){
                is LoadState.NotLoading->{
                    if (pagingListAdapter?.itemCount==0){
                        showEmptyState()
                        setEmptyStateText(getString(R.string.error_state_title),getString(R.string.empty_title))
                    }
                    else showSuccessState()
                }
                is LoadState.Loading-> {
                    showLoadingState()
                    println("still loading ")
                }
                is LoadState.Error->{
                    val errorMessage=when((it.refresh as LoadState.Error).error){
                       is UnknownHostException, is SocketTimeoutException, is ConnectException->{
                            "loading of popular comics failed due to network error; check your internet connection"
                        }
                        else-> "load state is error: ${ (it.refresh as LoadState.Error).error.message}"
                    }
                    fragmentBinding.swipeRefreshLayoutContainer.showSnackBar(errorMessage)
                    fragmentBinding.errorStateLayout.emptyErrorStateTitle.text = getString(R.string.error_state_title)
                    fragmentBinding.errorStateLayout.emptyErrorStateSubtitle.text = errorMessage
                    showErrorState()

                }
            }
            setUpSwipeRefreshWidgetState(fragmentBinding.baseFragSwipeRefresh.isRefreshing && it.refresh is LoadState.Loading)
          //  fragmentBinding.baseFragSwipeRefresh.isRefreshing = fragmentBinding.baseFragSwipeRefresh.isRefreshing && it.refresh is LoadState.Loading
        }
    }
    private fun constructUiMeasureSpec(screenSize: ScreenSize):UiMeasureSpec = when(screenSize){
            ScreenSize.COMPACT->{
                /* layout grid uses 4 columns so for the recycler view's grid layout we need 2 columns */
                /* layout grid uses 16.dp gutter for 4 columns so for the recycler view's grid layout spacing in between columns ought to be 4.dp */
                UiMeasureSpec(recyclerViewColumns = defaultNumberOfColumns, recyclerViewMargin = defaultSpacing)
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
    private fun setUpBasicFragmentsComponents(){
        fragmentBinding.baseFragToolbar.title = getString(R.string.popular_comics)
        fragmentBinding.baseFragSwipeRefresh.setOnRefreshListener { pagingListAdapter?.refresh() }
    }
    private  fun initializePagedDataRecyclerView(uiMeasureSpecState: UiMeasureSpec){
        fragmentBinding.baseFragRecyclerView.apply {
            setHasFixedSize(true)
            scrollToTop()
            adapter = listAdapter
            layoutManager=gridLayoutManager(spanCount = uiMeasureSpecState.recyclerViewColumns)

          addItemDecoration(RecyclerViewItemDecoration(includeEdge = true, spanCount = uiMeasureSpecState.recyclerViewColumns, spacing = requireActivity().convertToPxFromDp(uiMeasureSpecState.recyclerViewMargin)))
        }
    }
    private fun setUpSwipeRefreshWidgetState(isRefreshing:Boolean){
        fragmentBinding.baseFragSwipeRefresh.isRefreshing=isRefreshing
    }

    private fun setEmptyStateText(title: String, subtitle: String) {
        fragmentBinding.emptyStateLayout.emptyErrorStateTitle.text = title
        fragmentBinding.emptyStateLayout.emptyErrorStateSubtitle.text = subtitle
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        pagingListAdapter = null
    }

}

package com.gibsonruitiari.asobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.gibsonruitiari.asobi.common.ScreenSize
import com.gibsonruitiari.asobi.common.utils.RecyclerViewItemDecoration
import com.gibsonruitiari.asobi.common.utils.convertToPxFromDp
import com.gibsonruitiari.asobi.databinding.FragmentFirstBinding
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.PagedPopularComicsAdapter
import com.gibsonruitiari.asobi.presenter.uiModels.UiMeasureSpec
import com.gibsonruitiari.asobi.presenter.uiModels.ViewComics
import com.gibsonruitiari.asobi.presenter.viewmodels.MainActivityViewModel
import com.gibsonruitiari.asobi.presenter.viewmodels.PopularComicsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentBinding get() = _binding!!
    private val activityMainViewModel:MainActivityViewModel by viewModel()
    private val popularComicsViewModel:PopularComicsViewModel by viewModel()

 companion object{
     private  const val  defaultNumberOfColumns =2 // by default
     private const val defaultSpacing = 16
     private const val mediumNumberOfColumns = 4
     private const val mediumSpacing = 24
     private const val extendedNumberOfColumns = 6
     private const val extendedSpacing = 32
 }
    private var uiMeasureSpec:UiMeasureSpec?=null

    private val itemEventCallback = object:PagedPopularComicsAdapter.ItemEventCallback{
        override fun onComicClicked(comics: ViewComics) {
            Toast.makeText(requireContext(), "clicked: ${comics.comicName}", Toast.LENGTH_SHORT).show()
        }

        override fun onComicLongClicked(comics: ViewComics) {
            Toast.makeText(requireContext(), "long clicked: ${comics.comicName}", Toast.LENGTH_SHORT).show()
        }
    }
    private val pagedPopularComicsAdapter:PagedPopularComicsAdapter = PagedPopularComicsAdapter(callback = itemEventCallback)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return fragmentBinding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   binding.buttonFirst.setOnClickListener { findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment) }
        initializeSwipeRefreshListener()
        viewLifecycleOwner.lifecycleScope.launch {
            // collect uiMeasureSpec
            activityMainViewModel.uiMeasureSpecState.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect{
                setUpRecyclerView(it)
            }
            repeatOnLifecycle(Lifecycle.State.STARTED){
                // collect paged data
                popularComicsViewModel.pagedList.collect{
                    updatePagedList(it)
                }
                // collect load states
                pagedPopularComicsAdapter.loadStateFlow.collect{
                    updateRefreshState(it)
                }

                activityMainViewModel.screenWidthState.collect{
                   uiMeasureSpec=  when(it){
                        ScreenSize.COMPACT->{
                            /* layout grid uses 4 columns so for the recycler view's grid layout we need 2 columns */
                            /* layout grid uses 16.dp gutter so for the recycler view's grid layout spacing in between columns ought to be 16.dp */
                            UiMeasureSpec(recyclerViewColumns = defaultNumberOfColumns, recyclerViewMargin = defaultSpacing)
                        }
                        ScreenSize.MEDIUM->{
                            /* layout grid uses 8 columns so for the recycler view's grid layout we need 4 columns */
                            /* layout grid uses 24.dp gutter so for the recycler view's grid layout spacing in between columns ought to be 24.dp */
                            UiMeasureSpec(recyclerViewColumns = mediumNumberOfColumns, recyclerViewMargin = mediumSpacing)
                        }
                        ScreenSize.EXPANDED->{
                            /* layout grid uses 12 columns so for the recycler view's grid layout we need 6 columns */
                            UiMeasureSpec(recyclerViewColumns = extendedNumberOfColumns, recyclerViewMargin = extendedSpacing)
                        }
                    }
                    uiMeasureSpec?.let {
                        // update the value
                        activityMainViewModel.setUiMeasureSpec(it)
                    }

                }
            }
        }


    }
    private fun setUpRecyclerView(uiMeasureSpecState: UiMeasureSpec){
        // set up recycler view
        fragmentBinding.baseFragRecyclerView.apply {
            adapter = pagedPopularComicsAdapter
            layoutManager = GridLayoutManager(requireContext(),uiMeasureSpecState.recyclerViewColumns)
            addItemDecoration(RecyclerViewItemDecoration(includeEdge = true, spanCount = uiMeasureSpecState.recyclerViewColumns,
            spacing = requireActivity().convertToPxFromDp(uiMeasureSpecState.recyclerViewMargin)))

        }
    }

   private fun updateRefreshState(loadState:CombinedLoadStates){
        when(loadState.refresh){
            is LoadState.NotLoading->{
                if (pagedPopularComicsAdapter.itemCount==0) {
                    showEmptyState()
                    setEmptyStateText(getString(R.string.empty_title),getString(R.string.empty_subtitle))
                    // retry perhaps
                   retryLoadingOfData()

                }else{
                    showSuccessState()
                }
            }
            is LoadState.Loading->showLoadingState()
            is LoadState.Error->{
                val errorState = loadState.refresh as LoadState.Error
                println("error: ${errorState.error.message}")
                fragmentBinding.errorStateLayout.emptyErrorStateTitle.text = getString(R.string.error_state_title)
                fragmentBinding.errorStateLayout.emptyErrorStateSubtitle.text =   errorState.error.message
                showErrorState()
               retryLoadingOfData()
            }
        }
        fragmentBinding.baseFragSwipeRefresh.isRefreshing= fragmentBinding.baseFragSwipeRefresh.isRefreshing && loadState.refresh is LoadState.Loading
    }
    private fun retryLoadingOfData(){
        with(fragmentBinding.emptyStateLayout.retryButton){
            isVisible=true
            setOnClickListener {
                pagedPopularComicsAdapter.retry()
            }
        }
    }
   private suspend fun updatePagedList(pagedList:PagingData<ViewComics>){
        // submit list to adapter
        pagedPopularComicsAdapter.submitData(pagedList)
    }
    private fun initializeSwipeRefreshListener(){
        fragmentBinding.baseFragSwipeRefresh.setOnRefreshListener {
            pagedPopularComicsAdapter.refresh()
        }
    }
    private fun setEmptyStateText(title:String,subtitle:String){
        with(fragmentBinding){
            emptyStateLayout.emptyErrorStateTitle.text=title
            emptyStateLayout.emptyErrorStateSubtitle.text= subtitle
        }
    }
    private fun showSuccessState(){
        with(fragmentBinding){
            baseFragRecyclerView.isVisible=true
            errorStateLayout.root.isVisible=false
            emptyStateLayout.root.isVisible=false
            // hide contentLoading
            fragmentBinding.contentLoadingLayout.hide()
        }
    }

    private fun showEmptyState(){
        fragmentBinding.baseFragRecyclerView.isVisible = false
        fragmentBinding.errorStateLayout.root.isVisible = false
        fragmentBinding.emptyStateLayout.root.isVisible = true
        fragmentBinding.contentLoadingLayout.hide()
    }
    private fun showErrorState(){
        fragmentBinding.baseFragRecyclerView.isVisible = false
        fragmentBinding.errorStateLayout.root.isVisible = true
        fragmentBinding.emptyStateLayout.root.isVisible = false
        fragmentBinding.contentLoadingLayout.hide()
    }
    private fun showLoadingState(){
        fragmentBinding.baseFragRecyclerView.isVisible = false
        fragmentBinding.errorStateLayout.root.isVisible = false
        fragmentBinding.emptyStateLayout.root.isVisible = false
        fragmentBinding.contentLoadingLayout.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
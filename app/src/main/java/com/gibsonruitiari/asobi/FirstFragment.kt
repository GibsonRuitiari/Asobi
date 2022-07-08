package com.gibsonruitiari.asobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentBinding get() = _binding!!
    private val activityMainViewModel:MainActivityViewModel by viewModel()
    private val popularComicsViewModel:PopularComicsViewModel by viewModel()
 companion object{
     private  const val  defaultNumberOfColumns =2 // by default
     private const val defaultSpacing = 4
     private const val mediumNumberOfColumns = 4
     private const val mediumSpacing = 8
     private const val extendedNumberOfColumns = 6
     private const val extendedSpacing = 16
 }
    private var uiMeasureSpec:UiMeasureSpec?=null


    private var pagedPopularComicsAdapter:PagedPopularComicsAdapter =  PagedPopularComicsAdapter(onComicClicked = {
        Toast.makeText(requireContext(),"${it.comicLink} clicked",Toast.LENGTH_SHORT).show() })


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
                /*Observe uiMeasureSpec state data */
                launch {
                    activityMainViewModel.uiMeasureSpecState.collectLatest {
                        initializePagedDataRecyclerView(it)
                    }
                }

                /* Observe paged data */
                launch {
                    popularComicsViewModel.pagedList.collectLatest{
                        pagedPopularComicsAdapter.submitData(it)
                    }
                }
                launch {
                    /*Observe screen size data */
                    activityMainViewModel.screenWidthState.collectLatest{
                        uiMeasureSpec= constructUiMeasureSpec(it)
                        uiMeasureSpec?.let {
                            // update the value
                            activityMainViewModel.setUiMeasureSpec(it)
                        }

                    }
                }

            }
        }
        fragmentBinding.baseFragToolbar.title = getString(R.string.popular_comics)
        observeStates()
        fragmentBinding.baseFragSwipeRefresh.setOnRefreshListener {
            pagedPopularComicsAdapter.refresh()
        }

    }
    private fun observeStates(){
        pagedPopularComicsAdapter.addLoadStateListener {
            when(it.refresh){
                is LoadState.NotLoading->{
                    if (pagedPopularComicsAdapter.itemCount==0) println("failed to load data; data is empty")
                    else println("managed to load data")
                }
                is LoadState.Loading-> println("data is loading")
                is LoadState.Error->{
                    when((it.refresh as LoadState.Error).error){
                        is UnknownHostException, is SocketTimeoutException, is ConnectException->{
                            println("loading of popular comics failed due to network error; check your internet connection")
                        }
                        else-> println("load state is error: ${ (it.refresh as LoadState.Error).error.message}")
                    }
                }
            }
            fragmentBinding.baseFragSwipeRefresh.isRefreshing = fragmentBinding.baseFragSwipeRefresh.isRefreshing && it.refresh is LoadState.Loading
        }
    }
    private fun constructUiMeasureSpec(screenSize: ScreenSize):UiMeasureSpec = when(screenSize){
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
    private  fun initializePagedDataRecyclerView(uiMeasureSpecState: UiMeasureSpec){
        fragmentBinding.baseFragRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pagedPopularComicsAdapter
            layoutManager = GridLayoutManager(requireContext(),uiMeasureSpecState.recyclerViewColumns)
          addItemDecoration(RecyclerViewItemDecoration(includeEdge = true, spanCount = uiMeasureSpecState.recyclerViewColumns, spacing = requireActivity().convertToPxFromDp(uiMeasureSpecState.recyclerViewMargin)))
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

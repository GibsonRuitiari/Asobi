package com.gibsonruitiari.asobi.ui.discovercomics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.databinding.DiscoverComicsFragmentBinding
import com.gibsonruitiari.asobi.ui.MainNavigationFragment
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.extensions.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("UNCHECKED_CAST")
class DiscoverFragment:MainNavigationFragment() {
    private var _binding:DiscoverComicsFragmentBinding?=null
    private val discoverComicsFragmentBinding:DiscoverComicsFragmentBinding get() = _binding!!
    private var BindingViewHolder<ComicItemLayoutBinding>.completedComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutBinding>.ongoingComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutBinding>.latestComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutBinding>.popularComics by viewHolderDelegate<ViewComics>()

    private fun BindingViewHolder<ComicItemLayoutBinding>.bindCompletedComics(viewComics: ViewComics){
        this.completedComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private fun BindingViewHolder<ComicItemLayoutBinding>.bindPopularComics(viewComics: ViewComics){
        this.popularComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private fun BindingViewHolder<ComicItemLayoutBinding>.bindOngoingComics(viewComics: ViewComics){
        this.ongoingComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private fun BindingViewHolder<ComicItemLayoutBinding>.bindLatestComics(viewComics: ViewComics){
        this.latestComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private val latestComicsAdapter = listAdapterOf(initialItems = emptyList(),
    viewHolderCreator = {
        parent: ViewGroup, _: Int ->
        parent.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener {
                Toast.makeText(requireContext(), "${latestComics.comicLink} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }, viewHolderBinder = {holder:BindingViewHolder<ComicItemLayoutBinding>, item:ViewComics, _ ->
            holder.bindLatestComics(item)
        })
    private val popularComicsAdapter = listAdapterOf(initialItems = emptyList(),
    viewHolderCreator = {parent: ViewGroup, _: Int ->
        parent.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener {
                Toast.makeText(requireContext(), "${popularComics.comicLink} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }, viewHolderBinder = {holder: RecyclerView.ViewHolder, item: ViewComics, _: Int ->
            (holder as BindingViewHolder<ComicItemLayoutBinding>).bindPopularComics(item)
        })
    private val completedComicsAdapter = listAdapterOf(initialItems = emptyList(),
    viewHolderCreator = {parent, _ ->
        parent.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener {        Toast.makeText(requireContext(), "${completedComics.comicLink} clicked", Toast.LENGTH_SHORT).show() }
        }
    }, viewHolderBinder = {holder:RecyclerView.ViewHolder,item:ViewComics,_ ->
            (holder as BindingViewHolder<ComicItemLayoutBinding>).bindCompletedComics(item)
        })
    private val ongoingComicsAdapter = listAdapterOf(initialItems = emptyList(),
    viewHolderCreator = {parent, _ ->
        parent.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener {
                Toast.makeText(requireContext(), "${ongoingComics.comicLink} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }, viewHolderBinder = {holder:RecyclerView.ViewHolder,item:ViewComics,_->
            (holder as BindingViewHolder<ComicItemLayoutBinding>).bindOngoingComics(item)
        })

    private val discoverViewModel:DiscoverViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DiscoverComicsFragmentBinding.inflate(inflater,container,false)
        return discoverComicsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState==null){
            /* Fragment is being shown for the first time/a new instance of this fragment is created hence do apply the insets accordingly  */
            discoverComicsFragmentBinding.coordinatorLayout.postDelayed({ discoverComicsFragmentBinding.coordinatorLayout.requestApplyInsetsWhenAttached() },500)
        }
        launchAndRepeatWithViewLifecycle {
            /* Observe the state from view model  */
            launch {
                discoverViewModel.observeState().collectLatest {
                    when{
                        it.isLoading-> onLoadingShowLoadingLayout()
                        else->{
                            val completedComics = it.comicsData.completedComics.comicsData
                            val popularComics = it.comicsData.popularComics.comicsData
                            val latestComics = it.comicsData.latestComics.comicsData
                            val ongoingComics = it.comicsData.ongoingComics.comicsData
                            when{
                                completedComics.isEmpty() || popularComics.isEmpty() || latestComics.isEmpty() || ongoingComics.isEmpty()->onDataEmptyShowEmptyLayout()
                                else->{
                                    completedComicsAdapter.submitList(completedComics)
                                    popularComicsAdapter.submitList(popularComics)
                                    ongoingComicsAdapter.submitList(ongoingComics)
                                    latestComicsAdapter.submitList(latestComics)
                                    onDataLoadedSuccessfullyShowDataLayout()
                                }
                            }
                        }
                    }
                }
            }

            /* Observe side effects too */
            launch {
                discoverViewModel.observeSideEffect().collect {
                    when(it){
                        is DiscoverComicsSideEffect.Error ->{
                            discoverComicsFragmentBinding.errorStateLayout.emptyErrorStateTitle.text = getString(
                                R.string.error_state_title)
                            discoverComicsFragmentBinding.errorStateLayout.emptyErrorStateSubtitle.text= it.message
                            discoverComicsFragmentBinding.coordinatorLayout.showSnackBar(it.message)
                            onErrorShowErrorLayout()
                        }
                    }
                }
            }
        }
        setUpDiscoverFragmentToolbar()
        setUpDiscoverFragmentsRecyclerViews()
        setUpOnMoreLabelClickListeners()
    }
    /* Start: Set up ui components */
    private fun setUpDiscoverFragmentToolbar(){
        // inflate menu
        with(discoverComicsFragmentBinding.toolbar){
            inflateMenu(R.menu.discover_frag_menu)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.refresh_item->{
                        Toast.makeText(requireContext(),"refresh action clicked",Toast.LENGTH_SHORT).show()
                        true
                    }
                    else->false
                }
            }
        }
    }
    private fun setUpDiscoverFragmentsRecyclerViews(){
        val linearSnapHelper = LinearSnapHelper()
        with(discoverComicsFragmentBinding){
            with(latestComicsRecyclerView){
                linearSnapHelper.attachToRecyclerView(this)
                layoutManager = horizontalLayoutManager()
                adapter = latestComicsAdapter
                setHasFixedSize(true)

            }
            with(completedComicsRecyclerview){
                linearSnapHelper.attachToRecyclerView(this)
                layoutManager = horizontalLayoutManager()
                adapter = completedComicsAdapter
                setHasFixedSize(true)
            }
            with(ongoingComicsRecyclerView){
                linearSnapHelper.attachToRecyclerView(this)
                layoutManager = horizontalLayoutManager()
                adapter = ongoingComicsAdapter
                setHasFixedSize(true)
            }
            with(popularComicsRecyclerView){
                linearSnapHelper.attachToRecyclerView(this)
                layoutManager = horizontalLayoutManager()
                adapter = popularComicsAdapter
                setHasFixedSize(true)
            }
        }
    }
    /* Navigate to the specific fragment when more button is clicked */
    private fun setUpOnMoreLabelClickListeners(){
        with(discoverComicsFragmentBinding){
            completedComicsMoreText.setOnClickListener {
                findNavController().navigate(R.id.navigation_completed_comics)
            }
            popularComicsMoreText.setOnClickListener {
                findNavController().navigate(R.id.navigation_popular_comics)
            }
            completedComicsMoreText.setOnClickListener {
                findNavController().navigate(R.id.navigation_completed_comics)
            }
            ongoingComicsMoreText.setOnClickListener {
                findNavController().navigate(R.id.navigation_ongoing_comics)
            }
        }
    }
    /* End: Set up ui components */


    /* Start: Respond to events by showing the requisite state on the screen to the user */
    private fun onDataLoadedSuccessfullyShowDataLayout(){
        with(discoverComicsFragmentBinding){
            contentLoadingLayout.hide()
            emptyStateLayout.root.isVisible=false
            errorStateLayout.root.isVisible=false
            discoverFragmentContainer.isVisible=true
        }
    }
    private fun onErrorShowErrorLayout(){
        with(discoverComicsFragmentBinding){
            contentLoadingLayout.hide()
            discoverFragmentContainer.isVisible=false
            errorStateLayout.root.isVisible=true
            emptyStateLayout.root.isVisible=false
        }
    }
    private fun onDataEmptyShowEmptyLayout(){
        with(discoverComicsFragmentBinding){
            contentLoadingLayout.hide()
            discoverFragmentContainer.isVisible=false
            errorStateLayout.root.isVisible=false
            emptyStateLayout.root.isVisible=true
        }
    }
    private fun onLoadingShowLoadingLayout(){
        with(discoverComicsFragmentBinding){
            discoverFragmentContainer.isVisible=false
            errorStateLayout.root.isVisible=false
            emptyStateLayout.root.isVisible=false
            contentLoadingLayout.show()
        }
    }
    /* End: Respond to events by showing the requisite state on the screen to the user */

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
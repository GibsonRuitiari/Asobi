package com.gibsonruitiari.asobi.ui.discovercomics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutDiscoverBinding
import com.gibsonruitiari.asobi.databinding.DiscoverComicsFragmentBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.ItemMarginRecyclerViewDecorator
import com.gibsonruitiari.asobi.utilities.extensions.cancelIfActive
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.extensions.showSnackBar
import com.gibsonruitiari.asobi.utilities.extensions.requestApplyInsetsWhenAttached
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.extensions.horizontalLayoutManager
import com.gibsonruitiari.asobi.utilities.extensions.doOnApplyWindowInsets
import com.gibsonruitiari.asobi.utilities.extensions.loadPhotoUrl
import com.gibsonruitiari.asobi.utilities.logging.AsobiLogger
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.widgets.ErrorStateLayout
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("UNCHECKED_CAST")
class DiscoverFragment:Fragment() {
    private val discoverViewModel:DiscoverViewModel by viewModel()
    private val mainViewModel:MainActivityViewModel by sharedViewModel()
    private val logger: Logger by inject()
    private var isFragmentHidden:Boolean=true
    private var dataLoadingJob:Job?=null
    private var _discoverFragmentBinding:DiscoverComicsFragmentBinding?=null
    private val discoverFragmentBinding:DiscoverComicsFragmentBinding
    get() = _discoverFragmentBinding!!
    private val discoverFragmentCompletedComicsRecyclerView = discoverFragmentBinding.completedComicsRecyclerview




    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState==null) return
        isFragmentHidden=savedInstanceState.getBoolean(isFragmentHiddenTag,true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFragmentHiddenTag,isFragmentHidden)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _discoverFragmentBinding=DiscoverComicsFragmentBinding.inflate(inflater,container,false)
        return discoverFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null) applyWindowInsetsToParentContainerWhenFragmentIsAttached()
        setUpDiscoverFragmentRecyclerViews()
        setUpDiscoverFragmentToolbarButtonsOnClickListener()
        onMoreLabelClickListeners()
    }

    private fun applyWindowInsetsToParentContainerWhenFragmentIsAttached(){
        /* Fragment is being shown for the first time/a new instance of this fragment is created hence do apply the insets accordingly  */
        discoverFragmentBinding.coordinatorLayout.postDelayed({discoverFragmentBinding.coordinatorLayout.requestApplyInsetsWhenAttached()},500)

    }
    /* Only load data when the fragment comes into view to avoid wastage of resources  */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isFragmentHidden=hidden
        doActionIfWeAreOnDebug { logger.i("is discover fragment hidden $hidden")}
        loadData()
    }
    private fun loadData(){
        if (isFragmentHidden){
            dataLoadingJob?.let {
                if (it.isActive){
                    logger.i("killing data loading job in discover fragment")
                    it.cancel()
                }
            }
        }else{
            observeStateFromViewModel()
        }
    }
    private fun observeStateFromViewModel(){
       dataLoadingJob?.cancel()
       dataLoadingJob=launchAndRepeatWithViewLifecycle {
           logger.i("data loading job in discover fragment initialized")
        observeData()
        observeSideEffects()
    }
}
    private  fun CoroutineScope.observeData(){
        launch {
            logger.i("observing data in observe data method discover fragment")
            discoverViewModel.observeState().collectLatest {
                when{
                    it.isLoading->{
                        onDataLoadingShowLoadingLayout()
                        logger.i("loading data")
                        discoverFragmentBinding.coordinatorLayout.showSnackBar(getString(R.string.loading_msg))
                    }
                    else->{
                            when{
                                it.isDataEmpty() ->{
                                    errorTitle.text=getString(R.string.error_state_title)
                                    errorSubtitle.text=getString(R.string.empty_subtitle)
                                    onErrorOrEmptyDataShowErrorEmptyLayout()
                                    logger.i("data is empty")
                                }
                                else->{
                                    it.submitDataRecyclerViewAdapterWhenItIsNotEmpty()
                                    logger.i("data fully loaded")
                                    onDataLoadedSuccessfullyShowDataLayout()
                                }
                            }
                        }
                    }
            }
        }
    }
    private  fun CoroutineScope.observeSideEffects(){
        launch{
         discoverViewModel.observeSideEffect().collectLatest {
             when(it){
                 is DiscoverComicsSideEffect.Error->{

                    // val retryButton =(discoverFragmentErrorLayout as ErrorStateLayout).retryButton
                     val errorMessage=if (it.message.contains(getString(R.string.domain_name),ignoreCase = true)) getString(R.string.network_error_msg) else it.message
                     errorSubtitle.text=errorMessage
                     errorTitle.text= getString(R.string.error_state_title)
                     discoverFragmentBinding.coordinatorLayout.showSnackBar(errorMessage)
                    onErrorOrEmptyDataShowErrorEmptyLayout()
                 }
             }
         }
        }
    }


     /* Start: Set up ui components */
    /* Navigate to notifications activity/settings activity when the buttons are clicked */
    private fun setUpDiscoverFragmentToolbarButtonsOnClickListener(){
    discoverFragmentBinding.notificationsButton.setOnClickListener { doActionIfWeAreOnDebug {discoverFragmentBinding.coordinatorLayout.showSnackBar("notifications"); logger.i("notifications button clicked") } }
    discoverFragmentBinding.settingsButton.setOnClickListener { doActionIfWeAreOnDebug {discoverFragmentBinding.coordinatorLayout.showSnackBar("settings"); logger.i("settings button clicked") } }
    }
    private fun setUpDiscoverFragmentRecyclerViews(){
     val linearSnapHelper = LinearSnapHelper()
        with(discoverFragmentBinding.latestComicsRecyclerView){
        linearSnapHelper.attachToRecyclerView(this)
        layoutManager = horizontalLayoutManager()
        adapter = completedComicsAdapter
        addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        setHasFixedSize(true)
    }
    with(discoverFragmentCompletedComicsRecyclerView){
        linearSnapHelper.attachToRecyclerView(this)
        layoutManager = horizontalLayoutManager()
        adapter = completedComicsAdapter
        addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        setHasFixedSize(true)
        // bottom padding to the last recycler-view
        doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
            val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = viewPaddingState.bottom+ systemInsets.bottom + resources.getDimension(R.dimen.default_padding).toInt())}
        }
    with(discoverFragmentBinding.popularComicsRecyclerView){
        linearSnapHelper.attachToRecyclerView(this)
        layoutManager = horizontalLayoutManager()
        adapter = popularComicsAdapter
        addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        setHasFixedSize(true)
    }
    }
    /* Navigate to the specific fragment when more button is clicked */
    private fun onMoreLabelClickListeners(){
    discoverFragmentBinding.popularComicsMoreText.setOnClickListener {
        doActionIfWeAreOnDebug {  discoverFragmentBinding.coordinatorLayout.showSnackBar("popular comics label clicked");logger.i("popular comics label clicked") } }
        discoverFragmentBinding.completedComicsMoreText.setOnClickListener { doActionIfWeAreOnDebug {  discoverFragmentBinding.coordinatorLayout.showSnackBar("completed comics label clicked");logger.i("completed comics label clicked") } }
        discoverFragmentBinding.latestComicsMoreText.setOnClickListener {
        mainViewModel.openLatestComicsScreen()
        doActionIfWeAreOnDebug {  discoverFragmentBinding.coordinatorLayout.showSnackBar("latest comics label clicked");logger.i("latest comics label clicked") } }
    }
    /* End: Set up ui components */



   /* Start: Respond to events by showing the requisite state on the screen to the user */
    private fun onDataLoadingShowLoadingLayout(){
        discoverFragmentBinding.contentLoadingLayout.apply { isVisible=true }.show()
       discoverFragmentBinding.errorStateLayout.root.isVisible=false
       discoverFragmentBinding.discoverFragmentAppBar.isVisible=false
       discoverFragmentBinding.discoverFragmentContainer.isVisible=false
    }
    private fun onErrorOrEmptyDataShowErrorEmptyLayout(){
        discoverFragmentBinding.contentLoadingLayout.apply { isVisible=false }.hide()
        discoverFragmentBinding.errorStateLayout.root.isVisible=true
        discoverFragmentBinding.discoverFragmentAppBar.isVisible=false
        discoverFragmentBinding.discoverFragmentContainer.isVisible=false
    }
    private fun onDataLoadedSuccessfullyShowDataLayout(){
        discoverFragmentBinding.contentLoadingLayout.apply { isVisible=false }.hide()
        discoverFragmentBinding.errorStateLayout.root.isVisible=false
        discoverFragmentBinding.discoverFragmentAppBar.isVisible=true
        discoverFragmentBinding.discoverFragmentContainer.isVisible=true
    }

    /* End: Respond to events by showing the requisite state on the screen to the user */

    /* Start: Utility functions related to DiscoverFragment  */
    private val errorTitle:AppCompatTextView
    get() =discoverFragmentBinding.errorStateLayout.emptyErrorStateTitle
    private val errorSubtitle:AppCompatTextView
    get() =discoverFragmentBinding.errorStateLayout.emptyErrorStateSubtitle

    private fun DiscoverComicsState.isDataEmpty( ):Boolean{
        val completedComics = comicsData.completedComics.comicsData
        val popularComics = comicsData.popularComics.comicsData
        val latestComics = comicsData.latestComics.comicsData
        val ongoingComics = comicsData.ongoingComics.comicsData
        val comicsByGenre = comicsData.comicsByGenre.comicsData

        val completedComicsLoadingState=comicsData.completedComics.isLoading
        val popularComicsLoadingState=comicsData.popularComics.isLoading
        val ongoingComicsLoadingState=comicsData.ongoingComics.isLoading
        val comicsByGenreComicsLoadingState= comicsData.comicsByGenre.isLoading
        val latestComicsLoadingState=comicsData.latestComics.isLoading
        return (completedComics.isEmpty() && !completedComicsLoadingState && popularComics.isEmpty() && !popularComicsLoadingState &&
                ongoingComics.isEmpty() && !ongoingComicsLoadingState && comicsByGenre.isEmpty() && comicsByGenreComicsLoadingState.not()
                &&latestComics.isEmpty() && latestComicsLoadingState.not())
    }
    private fun DiscoverComicsState.submitDataRecyclerViewAdapterWhenItIsNotEmpty(){
        val completedComics = comicsData.completedComics.comicsData
        val popularComics = comicsData.popularComics.comicsData
        val latestComics = comicsData.latestComics.comicsData
        val ongoingComics = comicsData.ongoingComics.comicsData
        val comicsByGenre = comicsData.comicsByGenre.comicsData
        logger.i("discover frag data size latest-> ${latestComics.size} ongoing -> ${ongoingComics.size} comics by genre ${comicsByGenre.size}")
        completedComicsAdapter.submitList(completedComics)
        popularComicsAdapter.submitList(popularComics)
        ongoingComicsAdapter.submitList(ongoingComics)
        latestComicsAdapter.submitList(latestComics)
        comicsByGenreAdapter.submitList(comicsByGenre)
    }

    /* End: Utility functions related to DiscoverFragment  */

   /*Start of recycler view's adapters + view-holder initialization  */
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.completedComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.ongoingComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.latestComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.popularComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.comicsByGenre by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindComicsByGenre(viewComics: ViewComics){
    this.comicsByGenre = viewComics
    with(binding){
        comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
    }
}
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindCompletedComics(viewComics: ViewComics){
        this.completedComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindPopularComics(viewComics: ViewComics){
        this.popularComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindOngoingComics(viewComics: ViewComics){
        this.ongoingComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindLatestComics(viewComics: ViewComics){
        this.latestComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }

    private val comicsByGenreAdapter:ListAdapter<ViewComics,BindingViewHolder<ComicItemLayoutDiscoverBinding>>
        get() {
            return listAdapterOf(initialItems = emptyList(),
                viewHolderCreator = {parent: ViewGroup, _: Int ->
                    parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                        itemView.setOnClickListener {  Toast.makeText(requireContext(), "${comicsByGenre.comicLink} clicked", Toast.LENGTH_SHORT).show() }
                    }
                }, viewHolderBinder = {holder:BindingViewHolder<ComicItemLayoutDiscoverBinding>, item:ViewComics,_->
                    holder.bindComicsByGenre(item)
                })
        }

    private val latestComicsAdapter:ListAdapter<ViewComics,BindingViewHolder<ComicItemLayoutDiscoverBinding>>
        get() {
            return listAdapterOf(initialItems = emptyList(),
                viewHolderCreator = { parent: ViewGroup, _: Int ->
                    parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                        itemView.setOnClickListener {
                            Toast.makeText(
                                requireContext(),
                                "${latestComics.comicLink} clicked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                viewHolderBinder = { holder: BindingViewHolder<ComicItemLayoutDiscoverBinding>, item: ViewComics, _ ->
                    holder.bindLatestComics(item)
                })
        }
    private val popularComicsAdapter :ListAdapter<ViewComics,BindingViewHolder<ComicItemLayoutDiscoverBinding>>
    get() {
        return listAdapterOf(initialItems = emptyList(),
            viewHolderCreator = { parent: ViewGroup, _: Int ->
                parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                    itemView.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            "${popularComics.comicLink} clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, viewHolderBinder = { holder: RecyclerView.ViewHolder, item: ViewComics, _: Int ->
                (holder as BindingViewHolder<ComicItemLayoutDiscoverBinding>).bindPopularComics(item)
            })
    }
    private val completedComicsAdapter:ListAdapter<ViewComics,BindingViewHolder<ComicItemLayoutDiscoverBinding>>
    get() {
        return listAdapterOf(initialItems = emptyList(),
            viewHolderCreator = { parent, _ ->
                parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                    itemView.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            "${completedComics.comicLink} clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, viewHolderBinder = { holder: RecyclerView.ViewHolder, item: ViewComics, _ ->
                (holder as BindingViewHolder<ComicItemLayoutDiscoverBinding>).bindCompletedComics(
                    item
                )
            })
    }
    private val ongoingComicsAdapter:ListAdapter<ViewComics,BindingViewHolder<ComicItemLayoutDiscoverBinding>>
    get() {
        return listAdapterOf(initialItems = emptyList(),
            viewHolderCreator = {parent, _ ->
                parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                    itemView.setOnClickListener {
                        Toast.makeText(requireContext(), "${ongoingComics.comicLink} clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            }, viewHolderBinder = {holder:RecyclerView.ViewHolder,item:ViewComics,_->
                (holder as BindingViewHolder<ComicItemLayoutDiscoverBinding>).bindOngoingComics(item)
            })
    }

    /* End of recycler view's adapters + view-holder initialization */
    override fun onDestroy() {
        super.onDestroy()
        _discoverFragmentBinding=null
    }
    companion object{
        private const val isFragmentHiddenTag ="discoverIsFragmentHidden"
    }
}
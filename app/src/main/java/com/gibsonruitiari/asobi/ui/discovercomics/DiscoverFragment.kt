package com.gibsonruitiari.asobi.ui.discovercomics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutDiscoverBinding
import com.gibsonruitiari.asobi.databinding.DiscoverComicsFragmentBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.ItemMarginRecyclerViewDecorator
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
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
    private val logger: Logger by inject()
    private var dataLoadingJob:Job?=null
    private val mainActivityViewModel:MainActivityViewModel by sharedViewModel()
    private val genreViewModel:ComicsByGenreViewModel by sharedViewModel()
    private lateinit var _discoverFragmentBinding:DiscoverComicsFragmentBinding
    private val discoverFragmentBinding:DiscoverComicsFragmentBinding
    get() = _discoverFragmentBinding

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
        updateParentContainerPadding()
        setUpDiscoverFragmentToolbar()
        onMoreLabelClickListeners()
        setUpDiscoverFragmentRecyclerViews()
        dynamicallyChangeStatusBarColorOnScroll()
        /* Load data once Fragment's view is created based on whether the fragment is hidden or not.
         This is needed since hide()&show() do not change the fragment's view lifecycle, without this,
         the first time the fragment is shown nothing will be shown */
         loadData(isHidden)
    }


    /* required for sanity check :-)  */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        loadData(hidden)
    }

     /* Start: Set up ui components */
    /* Navigate to notifications activity/settings activity when the buttons are clicked */
    private fun setUpDiscoverFragmentToolbar(){
   discoverFragmentBinding.greetingsText.text=discoverScreenGreetingMessage()
    discoverFragmentBinding.notificationsButton.setOnClickListener { doActionIfWeAreOnDebug {discoverFragmentBinding.coordinatorLayout.showSnackBar("notifications"); logger.i("notifications button clicked") } }
    discoverFragmentBinding.settingsButton.setOnClickListener { doActionIfWeAreOnDebug {discoverFragmentBinding.coordinatorLayout.showSnackBar("settings"); logger.i("settings button clicked") } }
    }
    private fun setUpRetryButtonClickListener(){
        with(discoverFragmentBinding.errorStateLayout.retryButton){
            isVisible=true
            setOnSafeClickListener {
                discoverFragmentBinding.coordinatorLayout.showSnackBar(getString(R.string.loading_msg))
                discoverViewModel.retry()
            }
        }
    }
    private fun setUpDiscoverFragmentRecyclerViews(){
     val linearSnapHelper = LinearSnapHelper()
        setUpMarvelComicsRecyclerView(linearSnapHelper)
        setUpCompletedComicsRecyclerView(linearSnapHelper)
        setUpPopularComicsRecyclerView(linearSnapHelper)
        setUpOngoingComicsRecyclerView(linearSnapHelper)
        setUpLatestComicsRecyclerView(linearSnapHelper)
        setUpDcComicsRecyclerView(linearSnapHelper)
    }
    private fun setUpLatestComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(discoverFragmentBinding.latestComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = latestComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        }
    }
    private fun setUpOngoingComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(discoverFragmentBinding.ongoingComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter=ongoingComicsAdapter
            layoutManager=horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        }
    }
    private fun setUpMarvelComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(discoverFragmentBinding.comicsByGenreRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter=comicsByGenreAdapter
            layoutManager=horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))

        }
    }
    private fun setUpCompletedComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(discoverFragmentBinding.completedComicsRecyclerview){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = completedComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))

        }
    }
    private fun setUpPopularComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(discoverFragmentBinding.popularComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = popularComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))

        }

    }
    private fun setUpDcComicsRecyclerView(linearSnapHelper: LinearSnapHelper) {
        with(discoverFragmentBinding.dcRecyclerView) {
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = dcComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(
                ItemMarginRecyclerViewDecorator(
                    resources.getDimension(R.dimen.default_padding).toInt()
                )
            )
            // bottom padding to the last recycler-view
            doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(bottom = viewPaddingState.bottom+ systemInsets.bottom + 20.dp)}

        }
    }
    /* Navigate to the specific fragment when more button is clicked */
    private fun onMoreLabelClickListeners(){
        with(discoverFragmentBinding){
            latestComicsMoreText.setOnClickListener {
                doActionIfWeAreOnDebug { logger.i("latest comics more text clicked") }
                mainActivityViewModel.openLatestComicsScreen()
            }
            ongoingComicsMoreText.setOnClickListener {
                mainActivityViewModel.openOngoingComicsScreen()
            }
            popularComicsMoreText.setOnClickListener {
                mainActivityViewModel.openPopularComicsScreen()
            }
            completedComicsMoreText.setOnClickListener {
                mainActivityViewModel.openCompletedComicsScreen()
            }
            marvelMoreText.setOnClickListener {
                // update genre
                genreViewModel.setGenre(Genres.MARVEL)
                mainActivityViewModel.openComicsByGenreScreen()
            }
            dcMoreText.setOnClickListener {
                genreViewModel.setGenre(Genres.DC_COMICS)
                doActionIfWeAreOnDebug { logger.i("dc clicked") }
                mainActivityViewModel.openComicsByGenreScreen()
            }

        }
    }
    /* End: Set up ui components */



   /* Start: Respond to events by showing the requisite state on the screen to the user */
    private fun onDataLoadingShowLoadingLayout(){
       discoverFragmentBinding.contentLoadingLayout.apply { isVisible=true }.show()
       discoverFragmentBinding.errorStateLayout.root.isVisible=false
       discoverFragmentBinding.discoverFragmentContainer.isVisible=false
    }
    private fun onErrorOrEmptyDataShowErrorEmptyLayout(){
        discoverFragmentBinding.contentLoadingLayout.apply { isVisible=false }.hide()
        discoverFragmentBinding.errorStateLayout.root.isVisible=true
        discoverFragmentBinding.discoverFragmentContainer.isVisible=false
    }
    private fun onDataLoadedSuccessfullyShowDataLayout(){
        discoverFragmentBinding.contentLoadingLayout.apply { isVisible=false }.hide()
        discoverFragmentBinding.errorStateLayout.root.isVisible=false
        discoverFragmentBinding.discoverFragmentContainer.isVisible=true
    }

    /* End: Respond to events by showing the requisite state on the screen to the user */



    /* Start: Utility functions related to DiscoverFragment  */
    private val errorTitle:AppCompatTextView
    get() =discoverFragmentBinding.errorStateLayout.emptyErrorStateTitle
    private val errorSubtitle:AppCompatTextView
    get() =discoverFragmentBinding.errorStateLayout.emptyErrorStateSubtitle

    private fun loadData(hidden: Boolean){
        if (hidden){
            dataLoadingJob?.cancelIfActive()
        }else{
            observeStateFromViewModel()
        }
    }
    /**
     * Helps to us to cancel and restart the job whenever the fragment's visibility state changes
     * Albeit, the view model outlives the fragment's lifecycle so in most cases,
     * the same instance of state flow is used everytime we restart/initialize the job
     * The purpose of calling [discoverViewModel.retry()] is to fetch new data whenever the fragment's
     * visibility changes, however we are using state-flow that caches the most recent value,
     * and if the value hasn't changed, the state-flow will return the same data instance
     * Also we don't have to cancel and initialize the job in onStart and onStop since flow.withLifecycle handles
     * that for us automatically.
     * */
    private fun observeStateFromViewModel() {
        dataLoadingJob?.cancel()
        discoverViewModel.retry()
        dataLoadingJob = launchAndRepeatWithViewLifecycle {
            observeData()
            observeSideEffects()
        }
    }
    private  fun CoroutineScope.observeData(){
        launch {
            discoverViewModel.observeState()
                .collectLatest {
                    when{
                        it.isLoading->{
                            onDataLoadingShowLoadingLayout()
                        }
                        else->{
                            when{
                                !it.isDataEmpty() ->{
                                    it.submitDataRecyclerViewAdapterWhenItIsNotEmpty()
                                    onDataLoadedSuccessfullyShowDataLayout()
                                }
                                else->{
                                    errorTitle.text=getString(R.string.error_state_title)
                                    errorSubtitle.text=getString(R.string.empty_subtitle)
                                    onErrorOrEmptyDataShowErrorEmptyLayout()
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
                        val errorMessage=if (it.message.contains(getString(R.string.domain_name),ignoreCase = true)) getString(R.string.network_error_msg) else it.message
                        errorSubtitle.text=errorMessage
                        errorTitle.text= getString(R.string.error_state_title)
                        onErrorOrEmptyDataShowErrorEmptyLayout()
                        setUpRetryButtonClickListener()
                    }
                }
            }
        }
    }
    private fun dynamicallyChangeStatusBarColorOnScroll(){
        discoverFragmentBinding.discoverFragmentContainer.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY>0){
                changeStatusBarToTransparentInFragment(resources.getColor(R.color.transparent,null))
            }else{
                changeStatusBarToTransparentInFragment(resources.getColor(R.color.black,null))
            }
        })
    }
    private fun applyWindowInsetsToParentContainerWhenFragmentIsAttached(){
        /* Fragment is being shown for the first time/a new instance of this fragment is created hence do apply the insets accordingly  */
        discoverFragmentBinding.coordinatorLayout.postDelayed({discoverFragmentBinding.coordinatorLayout.requestApplyInsetsWhenAttached()},500)
    }
    private fun updateParentContainerPadding() {
        with(discoverFragmentBinding.coordinatorLayout) {
            doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
                        .ime()
                )
                // pad the coordinator layout to ensure it stays above the nav bar
                view.updatePadding(bottom = viewPaddingState.bottom + systemInsets.bottom + 25.dp)
            }
        }
    }
    private fun DiscoverComicsState.isDataEmpty( ):Boolean{
        val completedComics = comicsData.completedComics.comicsData
        val popularComics = comicsData.popularComics.comicsData
        val latestComics = comicsData.latestComics.comicsData
        val ongoingComics = comicsData.ongoingComics.comicsData
        val marvelGenre = comicsData.marvelComics.comicsData
        val dcComics=comicsData.dcComics.comicsData

        val completedComicsLoadingState=comicsData.completedComics.isLoading
        val popularComicsLoadingState=comicsData.popularComics.isLoading
        val ongoingComicsLoadingState=comicsData.ongoingComics.isLoading
        val marvelComicsLoadingState= comicsData.marvelComics.isLoading
        val latestComicsLoadingState=comicsData.latestComics.isLoading
        val dcComicsLoadingState=comicsData.dcComics.isLoading

        return (completedComics.isEmpty() && !completedComicsLoadingState && popularComics.isEmpty() && !popularComicsLoadingState &&
                ongoingComics.isEmpty() && !ongoingComicsLoadingState && marvelGenre.isEmpty() && marvelComicsLoadingState.not()
                &&latestComics.isEmpty() && latestComicsLoadingState.not() &&dcComics.isEmpty() && dcComicsLoadingState.not())
    }
    private fun DiscoverComicsState.submitDataRecyclerViewAdapterWhenItIsNotEmpty(){
        val completedComics = comicsData.completedComics.comicsData
        val popularComics = comicsData.popularComics.comicsData
        val latestComics = comicsData.latestComics.comicsData
        val ongoingComics = comicsData.ongoingComics.comicsData
        val comicsByGenre = comicsData.marvelComics.comicsData
        val dcComics= comicsData.dcComics.comicsData
        completedComicsAdapter.submitList(completedComics)
        popularComicsAdapter.submitList(popularComics)
        ongoingComicsAdapter.submitList(ongoingComics)
        latestComicsAdapter.submitList(latestComics)
        comicsByGenreAdapter.submitList(comicsByGenre)
        dcComicsAdapter.submitList(dcComics)
    }
    private fun discoverScreenGreetingMessage():String{
        val hour by lazy { org.threeten.bp.LocalTime.now().hour }
        return when{
            hour<12 -> {
                discoverFragmentBinding.toolbar.background= resources.getDrawable(R.drawable.discover_screen_gradient_morning,null)
                getString(R.string.good_morning)
            }
            hour <17 ->{
                discoverFragmentBinding.toolbar.background= resources.getDrawable(R.drawable.discover_screen_gradient_afternoon,null)
                getString(R.string.good_afternoon)
            }
            else->{
                discoverFragmentBinding.toolbar.background= resources.getDrawable(R.drawable.discover_screen_gradient_evening,null)
                getString(R.string.good_evening)
            }
        }
    }


    /* End: Utility functions related to DiscoverFragment  */

   /*Start of recycler view's adapters + view-holder initialization  */
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.completedComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.ongoingComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.latestComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.popularComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.marvelComics by viewHolderDelegate<ViewComics>()
    private var BindingViewHolder<ComicItemLayoutDiscoverBinding>.dcComics by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindMarvelComics(viewComics: ViewComics){
    this.marvelComics = viewComics
    with(binding){
        comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
    }
}
    private fun BindingViewHolder<ComicItemLayoutDiscoverBinding>.bindDcComics(viewComics: ViewComics){
        this.dcComics = viewComics
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

    private val comicsByGenreAdapter = listAdapterOf(initialItems = emptyList(),
                viewHolderCreator = {parent: ViewGroup, _: Int ->
                    parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                        itemView.setOnClickListener {  Toast.makeText(requireContext(), "${marvelComics.comicLink} clicked", Toast.LENGTH_SHORT).show() }
                    }
                }, viewHolderBinder = {holder:BindingViewHolder<ComicItemLayoutDiscoverBinding>, item:ViewComics,_->
                    holder.bindMarvelComics(item)
                })


    private val latestComicsAdapter = listAdapterOf(initialItems = emptyList(),
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

    private val popularComicsAdapter = listAdapterOf(initialItems = emptyList(),
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

    private val completedComicsAdapter = listAdapterOf(initialItems = emptyList(),
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
    private val dcComicsAdapter = listAdapterOf(initialItems = emptyList(),
        viewHolderCreator = {parent, _ ->
            parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                itemView.setOnClickListener {
                    Toast.makeText(requireContext(), "${dcComics.comicLink} clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }, viewHolderBinder = {holder:RecyclerView.ViewHolder,item:ViewComics,_->
            (holder as BindingViewHolder<ComicItemLayoutDiscoverBinding>).bindDcComics(item)
        })
    private val ongoingComicsAdapter = listAdapterOf(initialItems = emptyList(),
            viewHolderCreator = {parent, _ ->
                parent.viewHolderFrom(ComicItemLayoutDiscoverBinding::inflate).apply {
                    itemView.setOnClickListener {
                        Toast.makeText(requireContext(), "${ongoingComics.comicLink} clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            }, viewHolderBinder = {holder:RecyclerView.ViewHolder,item:ViewComics,_->
                (holder as BindingViewHolder<ComicItemLayoutDiscoverBinding>).bindOngoingComics(item)
            })


    /* End of recycler view's adapters + view-holder initialization */

}
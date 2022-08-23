package com.gibsonruitiari.asobi.ui.discovercomics

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutDiscoverBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModel
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.*
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("UNCHECKED_CAST")
@VisibleForTesting
class DiscoverFragment:Fragment() {
    private val mainActivityViewModel:MainActivityViewModel by sharedViewModel()
    private val comicsByGenreViewModel: ComicsByGenreViewModel by viewModel(owner = { requireParentFragment() })
    private val comicFilterViewModel: ComicFilterViewModel
        get() = comicsByGenreViewModel
    private val discoverViewModel:DiscoverViewModel by viewModel()


    private val logger: Logger by inject()
    private var dataLoadingJob:Job?=null
    private var color:Int?=null

    /* Start of fragment view variables initialization  */
    private lateinit var rootView:DiscoverFragmentView
    private lateinit var latestComicsRecyclerView: RecyclerView
    private lateinit var popularComicsRecyclerView: RecyclerView
    private lateinit var completedComicsRecyclerView: RecyclerView
    private lateinit var ongoingComicsRecyclerView: RecyclerView
    private lateinit var marvelComicsRecyclerView: RecyclerView
    private lateinit var dcComicsRecyclerView: RecyclerView


    private lateinit var retryButton: MaterialButton
    private lateinit var loadingLayout: LoadingLayout
    private lateinit var errorEmptyLayout: ConstraintLayout
    private lateinit var errorTitleTextView: AppCompatTextView
    private lateinit var errorSubtitleTextView: AppCompatTextView


    private lateinit var discoverFragmentContainer:NestedScrollView
    private lateinit var discoverFragmentToolbar: Toolbar

    private lateinit var discoverFragmentToolbarGreetingsTextView:AppCompatTextView
    private lateinit var discoverFragmentToolbarNotificationsButton: AppCompatImageButton
    private lateinit var discoverFragmentToolbarSettingsButton: AppCompatImageButton

    private lateinit var latestComicsMoreText:AppCompatTextView
    private lateinit var popularComicsMoreText:AppCompatTextView
    private lateinit var completedComicsMoreText:AppCompatTextView
    private lateinit var ongoingComicsMoreText:AppCompatTextView
    private lateinit var marvelComicsMoreText:AppCompatTextView
    private lateinit var dcComicsMoreText:AppCompatTextView
    /* End of fragment view variables initialization  */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView=DiscoverFragmentView(requireContext())
        rootView=fragmentView
        discoverFragmentToolbar = fragmentView.discoverFragmentToolbar
        discoverFragmentContainer=fragmentView.discoverFragmentContainer

        discoverFragmentToolbarGreetingsTextView=fragmentView.discoverFragmentToolbarGreetingsTextView
        discoverFragmentToolbarNotificationsButton=fragmentView.discoverFragmentToolbarNotificationsButton
        discoverFragmentToolbarSettingsButton=fragmentView.discoverFragmentToolbarSettingsButton

        latestComicsMoreText = fragmentView.latestComicsMoreText
        popularComicsMoreText = fragmentView.popularComicsMoreText
        completedComicsMoreText = fragmentView.completedComicsMoreText
        ongoingComicsMoreText =fragmentView.ongoingComicsMoreText
        marvelComicsMoreText=fragmentView.marvelComicsMoreText
        dcComicsMoreText = fragmentView.dcComicsMoreText

        retryButton=fragmentView.retryButton

        errorTitleTextView=fragmentView.errorTitle
        errorSubtitleTextView=fragmentView.subtitleError
        loadingLayout=fragmentView.loadingStateLayout
        errorEmptyLayout=fragmentView.errorEmptyStateLayout

        dcComicsRecyclerView = fragmentView.dcComicsRecyclerView
        marvelComicsRecyclerView = fragmentView.marvelComicsRecyclerView
        ongoingComicsRecyclerView =fragmentView.ongoingComicsRecyclerView
        completedComicsRecyclerView = fragmentView.completedComicsRecyclerView
        latestComicsRecyclerView=fragmentView.latestComicsRecyclerView
        popularComicsRecyclerView = fragmentView.popularComicsRecyclerView

        return rootView
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
    discoverFragmentToolbarGreetingsTextView.text=discoverScreenGreetingMessage()
    discoverFragmentToolbarNotificationsButton.setOnClickListener { doActionIfWeAreOnDebug {rootView.showSnackBar("notifications"); logger.i("notifications button clicked") } }
    discoverFragmentToolbarSettingsButton.setOnClickListener { doActionIfWeAreOnDebug {rootView.showSnackBar("settings"); logger.i("settings button clicked") } }
    }
    private fun setUpRetryButtonClickListener(){
        with(retryButton){
            isVisible=true
            setOnSafeClickListener {
                rootView.showSnackBar(getString(R.string.loading_msg))
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
        with(latestComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = latestComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        }
    }
    private fun setUpOngoingComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(ongoingComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter=ongoingComicsAdapter
            layoutManager=horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        }
    }
    private fun setUpMarvelComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(marvelComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter=comicsByGenreAdapter
            layoutManager=horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))

        }
    }
    private fun setUpCompletedComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(completedComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = completedComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))

        }
    }
    private fun setUpPopularComicsRecyclerView(linearSnapHelper: LinearSnapHelper){
        with(popularComicsRecyclerView){
            linearSnapHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            adapter = popularComicsAdapter
            layoutManager = horizontalLayoutManager()
            addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))

        }

    }
    private fun setUpDcComicsRecyclerView(linearSnapHelper: LinearSnapHelper) {
        with(dcComicsRecyclerView) {
            linearSnapHelper.attachToRecyclerView(this)
            defaultRecyclerViewSetUp(paddingBottom = 20.dp, recyclerViewAdapter = dcComicsAdapter,itemDecoration = ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        }
    }
    /* Navigate to the specific fragment when more button is clicked */
    private fun onMoreLabelClickListeners(){
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
            marvelComicsMoreText.setOnClickListener {
                comicFilterViewModel.setGenre(Genres.MARVEL)
                mainActivityViewModel.openComicsByGenreScreen()
            }
            dcComicsMoreText.setOnClickListener {
                comicFilterViewModel.setGenre(Genres.DC_COMICS)
                mainActivityViewModel.openComicsByGenreScreen()
            }


    }
    /* End: Set up ui components */


   /* Start: Respond to events by showing the requisite state on the screen to the user */
    private fun onDataLoadingShowLoadingLayout(){
       loadingLayout.apply { isVisible=true }.show()
       errorEmptyLayout.isVisible=false
       discoverFragmentContainer.isVisible=false
    }
    private fun onErrorOrEmptyDataShowErrorEmptyLayout(){
        loadingLayout.apply { isVisible=false }.hide()
        errorEmptyLayout.isVisible=true
        discoverFragmentContainer.isVisible=false
    }
    private fun onDataLoadedSuccessfullyShowDataLayout(){
        loadingLayout.apply { isVisible=false }.hide()
        errorEmptyLayout.isVisible=false
        discoverFragmentContainer.isVisible=true
    }

    /* End: Respond to events by showing the requisite state on the screen to the user */



    /* Start: Utility functions related to DiscoverFragment  */
    private fun loadData(hidden: Boolean){
        if (hidden){
            dataLoadingJob?.cancelIfActive()
        }else{
            discoverFragmentContainer.scrollY=0
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
                                    errorTitleTextView.text=getString(R.string.error_state_title)
                                    errorSubtitleTextView.text=getString(R.string.empty_subtitle)
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
                        errorSubtitleTextView.text=errorMessage
                        errorTitleTextView.text= getString(R.string.error_state_title)
                        onErrorOrEmptyDataShowErrorEmptyLayout()
                        setUpRetryButtonClickListener()
                    }
                }
            }
        }
    }
    private fun dynamicallyChangeStatusBarColorOnScroll(){
        // line changed
        color?.let { changeStatusBarToTransparentInFragment(it) }
        discoverFragmentContainer.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY==0){
                doActionIfWeAreOnDebug { logger.i("scroll y=0 in scroll view") }
            }
            if (scrollY>0){
                changeStatusBarToTransparentInFragment(resources.getColor(R.color.transparent,null))
            }else{
                changeStatusBarToTransparentInFragment(color ?: resources.getColor(R.color.black,null))
            }
        })
    }
    private fun applyWindowInsetsToParentContainerWhenFragmentIsAttached(){
        /* Fragment is being shown for the first time/a new instance of this fragment is created hence do apply the insets accordingly  */
        rootView.postDelayed({rootView.requestApplyInsetsWhenAttached()},500)
    }
    private fun updateParentContainerPadding() {
        with(rootView) {
            doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
                        .ime()
                )
                // pad the coordinator layout to ensure it stays above the nav bar
                view.updatePadding(bottom = viewPaddingState.bottom + systemInsets.bottom + 10.dp)
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
                color= resources.getColor(R.color.carolina_blue,null)
                discoverFragmentToolbar.background= resources.getDrawable(R.drawable.discover_screen_gradient_morning,null)
                getString(R.string.good_morning)
            }
            hour <17 ->{
                color = resources.getColor(R.color.sun_yellow,null)
                discoverFragmentToolbar.background= resources.getDrawable(R.drawable.discover_screen_gradient_afternoon,null)
                getString(R.string.good_afternoon)
            }
            else->{
                color = resources.getColor(R.color.bright_orange,null)
                discoverFragmentToolbar.background= resources.getDrawable(R.drawable.discover_screen_gradient_evening,null)
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
                        itemView.setOnClickListener {  Toast.makeText(requireContext(), "${marvelComics.comicName} clicked", Toast.LENGTH_SHORT).show() }
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

    /**  Start of discover fragment view **/
    internal class DiscoverFragmentView constructor(context:Context):ParentFragmentsView(context) {
        lateinit var discoverFragmentContainer:NestedScrollView
        lateinit var discoverFragmentToolbar: Toolbar

        lateinit var discoverFragmentToolbarGreetingsTextView:AppCompatTextView
        lateinit var discoverFragmentToolbarNotificationsButton: AppCompatImageButton
        lateinit var discoverFragmentToolbarSettingsButton: AppCompatImageButton

        lateinit var latestComicsMoreText:AppCompatTextView
        lateinit var popularComicsMoreText:AppCompatTextView
        lateinit var completedComicsMoreText:AppCompatTextView
        lateinit var ongoingComicsMoreText:AppCompatTextView
        lateinit var marvelComicsMoreText:AppCompatTextView
        lateinit var dcComicsMoreText:AppCompatTextView

        private lateinit var latestComicsLabelText:AppCompatTextView
        private lateinit var ongoingComicsLabelText:AppCompatTextView
        private lateinit var popularComicsLabelText:AppCompatTextView
        private lateinit var completedComicsLabelText:AppCompatTextView
        private lateinit var marvelComicsLabelText:AppCompatTextView
        private lateinit var dcComicsLabelText:AppCompatTextView

        lateinit var latestComicsRecyclerView: RecyclerView
        lateinit var popularComicsRecyclerView: RecyclerView
        lateinit var completedComicsRecyclerView: RecyclerView
        lateinit var ongoingComicsRecyclerView: RecyclerView
        lateinit var marvelComicsRecyclerView: RecyclerView
        lateinit var dcComicsRecyclerView: RecyclerView

        init {
            discoverFragmentNestedScrollView(context)

        }
        private fun discoverFragmentNestedScrollView(context: Context){
            val nestedScrollView = NestedScrollView(context).apply {
                id= ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                visibility= View.GONE
                isFillViewport=true
                (layoutParams as LayoutParams).behavior= AppBarLayout.ScrollingViewBehavior()
            }
            discoverFragmentContainer=nestedScrollView
            discoverFragmentContainer.addView(discoverFragmentViewConstraintLayout(discoverFragmentContainer.context))
            addView(discoverFragmentContainer)

        }
        private fun discoverFragmentViewConstraintLayout(context: Context): ConstraintLayout {
            val constraintLayout = ConstraintLayout(context).apply {
                id= ViewCompat.generateViewId()
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                fitsSystemWindows=true
            }
            val constraintSet = ConstraintSet()
            val appBar=discoverFragmentViewAppBarLayout(constraintLayout.context,constraintSet)
            constraintLayout.addView(appBar)

            val latestComicsLabelRow =latestComicsLabelRow(constraintLayout.context,constraintSet,appBar.id)
            latestComicsLabelText=latestComicsLabelRow.first
            latestComicsMoreText=latestComicsLabelRow.second
            latestComicsRecyclerView=latestComicsRecyclerView(constraintLayout.context,constraintSet,latestComicsLabelText.id)
            constraintLayout.addView(latestComicsLabelText)
            constraintLayout.addView(latestComicsMoreText)
            constraintLayout.addView(latestComicsRecyclerView)

            val popularComicsLabelRow=popularComicsLabelsRow(constraintLayout.context,constraintSet, latestComicsRecyclerViewId = latestComicsRecyclerView.id)
            popularComicsLabelText=popularComicsLabelRow.first
            popularComicsMoreText=popularComicsLabelRow.second
            popularComicsRecyclerView=popularComicsRecyclerView(constraintLayout.context,constraintSet,popularComicsLabelText.id)
            constraintLayout.addView(popularComicsLabelText)
            constraintLayout.addView(popularComicsMoreText)
            constraintLayout.addView(popularComicsRecyclerView)

            val completedComicsLabelRow =completedComicsLabelRow(constraintLayout.context,constraintSet,
                popularComicsRecyclerViewId = popularComicsRecyclerView.id)
            completedComicsLabelText=completedComicsLabelRow.first
            completedComicsMoreText=completedComicsLabelRow.second
            completedComicsRecyclerView=completedComicsRecyclerView(constraintLayout.context,constraintSet,completedComicsLabelText.id)
            constraintLayout.addView(completedComicsLabelText)
            constraintLayout.addView(completedComicsMoreText)
            constraintLayout.addView(completedComicsRecyclerView)

            val ongoingComicsLabelRow = ongoingComicsLabelRow(constraintLayout.context,constraintSet,
                completedComicsRecyclerViewId = completedComicsRecyclerView.id)
            ongoingComicsLabelText=ongoingComicsLabelRow.first
            ongoingComicsMoreText =ongoingComicsLabelRow.second
            ongoingComicsRecyclerView=ongoingComicsRecyclerView(constraintLayout.context,constraintSet,ongoingComicsLabelText.id)
            constraintLayout.addView(ongoingComicsLabelText)
            constraintLayout.addView(ongoingComicsMoreText)
            constraintLayout.addView(ongoingComicsRecyclerView)

            val marvelComicsLabelRow = marvelComicsLabelRow(constraintLayout.context,constraintSet,
                ongoingComicsRecyclerViewId = ongoingComicsRecyclerView.id)
            marvelComicsLabelText=marvelComicsLabelRow.first
            marvelComicsMoreText=marvelComicsLabelRow.second
            marvelComicsRecyclerView=marvelComicsRecyclerView(constraintLayout.context,constraintSet,marvelComicsLabelText.id)
            constraintLayout.addView(marvelComicsLabelText)
            constraintLayout.addView(marvelComicsMoreText)
            constraintLayout.addView(marvelComicsRecyclerView)

            val dcComicsLabelRow = dcComicsLabelsRow(constraintLayout.context,constraintSet,marvelComicsRecyclerViewId = marvelComicsRecyclerView.id)
            dcComicsLabelText=dcComicsLabelRow.first
            dcComicsMoreText=dcComicsLabelRow.second
            dcComicsRecyclerView = dcComicsRecyclerView(constraintLayout.context,constraintSet, dcComicsLabelId = dcComicsLabelText.id)
            constraintLayout.addView(dcComicsLabelText)
            constraintLayout.addView(dcComicsMoreText)
            constraintLayout.addView(dcComicsRecyclerView)

            constraintSet.applyTo(constraintLayout)
            return constraintLayout
        }
        private fun discoverFragmentViewAppBarLayout(context: Context,constraintSet: ConstraintSet):AppBarLayout {
            val appBarLayout = AppBarLayout(context).apply {
                id= ViewCompat.generateViewId()
                setViewElevation()
                elevation= noElevation
                fitsSystemWindows=true
            }
            val appBarLayoutId= appBarLayout.id
            constraintSet.setViewLayoutParams(appBarLayoutId, ConstraintSet.MATCH_CONSTRAINT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet constrainTopToParent  appBarLayoutId
            constraintSet constrainStartToParent appBarLayoutId
            constraintSet constrainEndToParent  appBarLayoutId
            discoverFragmentToolbar=discoverFragmentViewToolbarLayout(appBarLayout.context)
            appBarLayout.addView(discoverFragmentToolbar)
            return appBarLayout
        }
        private fun discoverFragmentViewToolbarLayout(context: Context): Toolbar {
            val toolbar = Toolbar(context).apply {
                id= ViewCompat.generateViewId()
                layoutParams = AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toolbarHeight.dp)
                setViewElevation()
                elevation= noElevation
                (layoutParams as MarginLayoutParams).setMargins(noMargin,noMargin,noMargin,marginSmall)
                setTitleTextAppearance(context, R.style.TextAppearance_Asobi_Headline5)
            }
            val iconsRow = discoverFragmentToolbarLinearLayout(toolbar.context)
            toolbar.addView(iconsRow)
            return toolbar
        }
        private fun discoverFragmentToolbarLinearLayout(context: Context): LinearLayoutCompat {
            val linearLayout = LinearLayoutCompat(context).apply {
                layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dividerPadding= marginSmall
                dividerDrawable=context.resources.getDrawable(com.google.android.material.R.drawable.abc_list_divider_material,null)
                showDividers= LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
                orientation= LinearLayoutCompat.HORIZONTAL

            }
            discoverFragmentToolbarGreetingsTextView = AppCompatTextView(linearLayout.context).apply {
                id= ViewCompat.generateViewId()
                layoutParams= LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
                (layoutParams as LinearLayoutCompat.LayoutParams).setMargins(10.dp)
                setTextAppearance(R.style.TextAppearance_Asobi_Body1)
                setTextColor(context.getColor(R.color.white))
            }
            linearLayout.addView(discoverFragmentToolbarGreetingsTextView)
            discoverFragmentToolbarNotificationsButton = AppCompatImageButton(linearLayout.context).apply {
                id= ViewCompat.generateViewId()
                layoutParams = LinearLayoutCompat.LayoutParams(45.dp,45.dp)
                (layoutParams as MarginLayoutParams).setMargins(marginSmall,0, marginSmall,0)
                (layoutParams as LinearLayoutCompat.LayoutParams).gravity=Gravity.END
                scaleType= ImageView.ScaleType.CENTER_INSIDE
                contentDescription=context.getString(R.string.notifications_button)
                background=context.resources.getDrawable(R.color.transparent,null)
                setImageDrawable(context.resources.getDrawable(R.drawable.notification_bell,null))
                isFocusable=true
                isClickable=true
            }
            linearLayout.addView(discoverFragmentToolbarNotificationsButton)
            discoverFragmentToolbarSettingsButton= AppCompatImageButton(linearLayout.context).apply {
                id= ViewCompat.generateViewId()
                layoutParams = LinearLayoutCompat.LayoutParams(45.dp,45.dp)
                (layoutParams as MarginLayoutParams).setMargins(marginSmall,0, marginSmall,0)
                (layoutParams as LinearLayoutCompat.LayoutParams).gravity=Gravity.END
                scaleType= ImageView.ScaleType.CENTER_INSIDE
                contentDescription=context.getString(R.string.settings_button)
                background=context.resources.getDrawable(R.color.transparent,null)
                setImageDrawable(context.resources.getDrawable(R.drawable.settings_icon,null))
                isFocusable=true
                isClickable=true
            }
            linearLayout.addView(discoverFragmentToolbarSettingsButton)
            return linearLayout
        }

        private fun latestComicsLabelRow(context: Context, constraintSet: ConstraintSet,
                                         appBarId:Int):Pair<AppCompatTextView,
                AppCompatTextView>{
            val latestComicsLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.latest_comics)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val latestComicsLabelId=latestComicsLabel.id
            constraintSet.applyMargin(latestComicsLabelId, marginStart = marginSmall)
            constraintSet.setViewLayoutParams(latestComicsLabelId, ConstraintSet.WRAP_CONTENT, ConstraintSet.WRAP_CONTENT)

            constraintSet constrainStartToParent latestComicsLabelId
            constraintSet.connect(latestComicsLabelId, ConstraintSet.TOP, appBarId, ConstraintSet.BOTTOM)
            // constraintSet.connect(latestComicsLabelId,ConstraintSet.BOTTOM,latestComicsRecyclerViewId,ConstraintSet.TOP)
            val latestComicsMoreLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.more_string)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            constraintSet.setViewLayoutParams(latestComicsMoreLabel.id, ConstraintSet.WRAP_CONTENT, ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(latestComicsMoreLabel.id, marginEnd = marginSmall)
            constraintSet constrainEndToParent latestComicsMoreLabel.id
            constraintSet.connect(latestComicsMoreLabel.id, ConstraintSet.TOP, appBarId, ConstraintSet.BOTTOM)
            return latestComicsLabel to latestComicsMoreLabel
        }
        private fun latestComicsRecyclerView(context: Context, constraintSet: ConstraintSet,
                                             latestComicsLabelId:Int):RecyclerView{
            val recyclerView=RecyclerView(context).apply {
                id= ViewCompat.generateViewId() }
            val recyclerViewId= recyclerView.id
            constraintSet.setViewLayoutParams(recyclerViewId, ConstraintSet.MATCH_CONSTRAINT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(recyclerViewId, marginStart = marginSmall, marginEnd = marginSmall, marginTop = marginMedium)
            constraintSet constrainStartToParent recyclerViewId
            constraintSet constrainEndToParent recyclerViewId
            constraintSet.connect(recyclerViewId,
                ConstraintSet.TOP,latestComicsLabelId,
                ConstraintSet.BOTTOM)
            return recyclerView
        }

        private fun popularComicsLabelsRow(context: Context, constraintSet: ConstraintSet,
                                           latestComicsRecyclerViewId:Int):Pair<AppCompatTextView,AppCompatTextView>{
            val popularComicsLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.popular_comics)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val popularComicsLabelId= popularComicsLabel.id
            constraintSet.setViewLayoutParams(popularComicsLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(popularComicsLabelId, marginTop = marginMedium, marginStart = marginSmall)
            constraintSet constrainStartToParent popularComicsLabelId
            constraintSet.connect(popularComicsLabelId,
                ConstraintSet.TOP,latestComicsRecyclerViewId,
                ConstraintSet.BOTTOM)

            val popularComicsMoreLabel=AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.more_string)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val popularComicsMoreLabelId= popularComicsMoreLabel.id
            constraintSet.setViewLayoutParams(popularComicsMoreLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(popularComicsMoreLabelId, marginTop = marginMedium, marginEnd = marginSmall)
            constraintSet constrainEndToParent popularComicsMoreLabelId
            constraintSet.connect(popularComicsMoreLabelId,
                ConstraintSet.TOP, latestComicsRecyclerViewId,
                ConstraintSet.BOTTOM)
            return popularComicsLabel to popularComicsMoreLabel
        }
        private fun popularComicsRecyclerView(context: Context, constraintSet: ConstraintSet,
                                              popularComicsLabelId:Int):RecyclerView{
            val recyclerView = RecyclerView(context).apply {
                id= ViewCompat.generateViewId() }

            val recyclerViewId= recyclerView.id
            constraintSet.setViewLayoutParams(recyclerViewId,
                ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(recyclerViewId, marginStart = marginSmall, marginEnd = marginSmall, marginTop = marginMedium)
            constraintSet constrainStartToParent recyclerViewId
            constraintSet constrainEndToParent recyclerViewId
            constraintSet.connect(recyclerViewId, ConstraintSet.TOP,
                popularComicsLabelId, ConstraintSet.BOTTOM)
            return recyclerView
        }
        private fun completedComicsLabelRow(context: Context, constraintSet: ConstraintSet,
                                            popularComicsRecyclerViewId:Int):Pair<AppCompatTextView,AppCompatTextView>{
            val completedComicsLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.completed_comics)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val completedComicsLabelId= completedComicsLabel.id
            constraintSet.setViewLayoutParams(completedComicsLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(completedComicsLabelId, marginTop = marginMedium, marginStart = marginSmall)
            constraintSet constrainStartToParent completedComicsLabelId
            constraintSet.connect(completedComicsLabelId,
                ConstraintSet.TOP,popularComicsRecyclerViewId,
                ConstraintSet.BOTTOM)

            val completedComicsMoreLabel=AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.more_string)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val completedComicsMoreLabelId= completedComicsMoreLabel.id
            constraintSet.setViewLayoutParams(completedComicsMoreLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(completedComicsMoreLabelId, marginTop = marginMedium, marginEnd = marginSmall)
            constraintSet constrainEndToParent completedComicsMoreLabelId
            constraintSet.connect(completedComicsMoreLabelId,
                ConstraintSet.TOP, popularComicsRecyclerViewId,
                ConstraintSet.BOTTOM)
            return completedComicsLabel to completedComicsMoreLabel
        }
        private fun completedComicsRecyclerView(context: Context, constraintSet: ConstraintSet, completedComicsLabelId:Int):RecyclerView{
            val recyclerView = RecyclerView(context).apply {
                id= ViewCompat.generateViewId() }

            val recyclerViewId= recyclerView.id
            constraintSet.setViewLayoutParams(recyclerViewId,
                ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(recyclerViewId, marginStart = marginSmall, marginEnd = marginSmall, marginTop = marginMedium)
            constraintSet constrainStartToParent recyclerViewId
            constraintSet constrainEndToParent recyclerViewId
            constraintSet.connect(recyclerViewId,
                ConstraintSet.TOP, completedComicsLabelId,
                ConstraintSet.BOTTOM)
            return recyclerView
        }
        private fun ongoingComicsLabelRow(context: Context,
                                          constraintSet: ConstraintSet, completedComicsRecyclerViewId:Int):Pair<AppCompatTextView,AppCompatTextView>{
            val ongoingComicsLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.ongoing_comics)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val ongoingComicsLabelId= ongoingComicsLabel.id
            constraintSet.setViewLayoutParams(ongoingComicsLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(ongoingComicsLabelId, marginTop = marginMedium, marginStart = marginSmall)
            constraintSet constrainStartToParent ongoingComicsLabelId
            constraintSet.connect(ongoingComicsLabelId,
                ConstraintSet.TOP,completedComicsRecyclerViewId,
                ConstraintSet.BOTTOM)

            val ongoingComicsMoreLabel=AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.more_string)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val ongoingComicsMoreLabelId= ongoingComicsMoreLabel.id
            constraintSet.setViewLayoutParams(ongoingComicsMoreLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(ongoingComicsMoreLabelId, marginTop = marginMedium, marginEnd = marginSmall)
            constraintSet constrainEndToParent ongoingComicsMoreLabelId
            constraintSet.connect(ongoingComicsMoreLabelId,
                ConstraintSet.TOP, completedComicsRecyclerViewId,
                ConstraintSet.BOTTOM)
            return ongoingComicsLabel to ongoingComicsMoreLabel
        }
        private fun ongoingComicsRecyclerView(context: Context, constrainSet: ConstraintSet,
                                              ongoingComicsLabelId:Int):RecyclerView{
            val recyclerView = RecyclerView(context).apply {
                id= ViewCompat.generateViewId() }

            val recyclerViewId= recyclerView.id
            constrainSet.setViewLayoutParams(recyclerViewId,
                ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
            constrainSet.applyMargin(recyclerViewId, marginStart = marginSmall, marginEnd = marginSmall, marginTop = marginMedium)
            constrainSet constrainStartToParent recyclerViewId
            constrainSet constrainEndToParent recyclerViewId
            constrainSet.connect(recyclerViewId,
                ConstraintSet.TOP, ongoingComicsLabelId,
                ConstraintSet.BOTTOM)
            return recyclerView
        }
        private fun marvelComicsLabelRow(context: Context,
                                         constraintSet: ConstraintSet, ongoingComicsRecyclerViewId:Int):Pair<AppCompatTextView,AppCompatTextView>{
            val marvelComicsLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.marvel)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val marvelComicsLabelId= marvelComicsLabel.id
            constraintSet.setViewLayoutParams(marvelComicsLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(marvelComicsLabelId, marginTop = marginMedium, marginStart = marginSmall)
            constraintSet constrainStartToParent marvelComicsLabelId
            constraintSet.connect(marvelComicsLabelId,
                ConstraintSet.TOP,ongoingComicsRecyclerViewId,
                ConstraintSet.BOTTOM)

            val marvelComicsMoreLabel=AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.more_string)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val marvelComicsMoreLabelId= marvelComicsMoreLabel.id
            constraintSet.setViewLayoutParams(marvelComicsMoreLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(marvelComicsMoreLabelId, marginTop = marginMedium, marginEnd = marginSmall)
            constraintSet constrainEndToParent marvelComicsMoreLabelId
            constraintSet.connect(marvelComicsMoreLabelId,
                ConstraintSet.TOP, ongoingComicsRecyclerViewId,
                ConstraintSet.BOTTOM)
            return marvelComicsLabel to marvelComicsMoreLabel
        }
        private fun marvelComicsRecyclerView(context: Context,
                                             constrainSet: ConstraintSet, marvelComicsLabelId:Int):RecyclerView{
            val recyclerView = RecyclerView(context).apply {
                id= ViewCompat.generateViewId() }

            val recyclerViewId= recyclerView.id
            constrainSet.setViewLayoutParams(recyclerViewId,
                ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
            constrainSet.applyMargin(recyclerViewId, marginStart = marginSmall, marginEnd = marginSmall, marginTop = marginMedium)
            constrainSet constrainStartToParent recyclerViewId
            constrainSet constrainEndToParent recyclerViewId
            constrainSet.connect(recyclerViewId,
                ConstraintSet.TOP, marvelComicsLabelId,
                ConstraintSet.BOTTOM)
            return recyclerView
        }
        private fun dcComicsLabelsRow(context: Context, constraintSet: ConstraintSet, marvelComicsRecyclerViewId:Int):Pair<AppCompatTextView,AppCompatTextView>{
            val dcComicsLabel = AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.dc_comics)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val dcComicsLabelId= dcComicsLabel.id
            constraintSet.setViewLayoutParams(dcComicsLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(dcComicsLabelId, marginTop = marginMedium, marginStart = marginSmall)
            constraintSet constrainStartToParent dcComicsLabelId
            constraintSet.connect(dcComicsLabelId,
                ConstraintSet.TOP,marvelComicsRecyclerViewId,
                ConstraintSet.BOTTOM)

            val dcComicsMoreLabel=AppCompatTextView(context).apply {
                id= ViewCompat.generateViewId()
                text=context.getString(R.string.more_string)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                gravity= Gravity.CENTER
                isAllCaps=false
                setTextColor(context.getColor(R.color.white))
            }
            val dcComicsMoreLabelId= dcComicsMoreLabel.id
            constraintSet.setViewLayoutParams(dcComicsMoreLabelId,
                ConstraintSet.WRAP_CONTENT,
                ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(dcComicsMoreLabelId, marginTop = marginMedium, marginEnd = marginSmall)
            constraintSet constrainEndToParent dcComicsMoreLabelId
            constraintSet.connect(dcComicsMoreLabelId,
                ConstraintSet.TOP, marvelComicsRecyclerViewId,
                ConstraintSet.BOTTOM)
            return dcComicsLabel to dcComicsMoreLabel
        }
        private fun dcComicsRecyclerView(context: Context, constrainSet: ConstraintSet, dcComicsLabelId:Int):RecyclerView{
            val recyclerView = RecyclerView(context).apply { id= ViewCompat.generateViewId() }
            val recyclerViewId= recyclerView.id
            constrainSet.setViewLayoutParams(recyclerViewId,
                ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
            constrainSet.applyMargin(recyclerViewId, marginStart = marginSmall, marginEnd = marginSmall, marginTop = marginMedium)
            constrainSet constrainStartToParent recyclerViewId
            constrainSet constrainEndToParent recyclerViewId
            constrainSet.connect(recyclerViewId, ConstraintSet.TOP, dcComicsLabelId, ConstraintSet.BOTTOM)
            return recyclerView

        }

        private fun View.setViewElevation(){
            val elevationAnimator = StateListAnimator()
            elevationAnimator.addState(IntArray(0),
                ObjectAnimator.ofFloat(this, "elevation", noElevation))
            stateListAnimator=elevationAnimator
        }
        companion object{
            const val toolbarIconsDefaultWidth =0
            const val toolbarIconsDefaultHeight =30
            const val toolbarHeight=145
        }
    }
    /**  End of discover fragment view **/
}
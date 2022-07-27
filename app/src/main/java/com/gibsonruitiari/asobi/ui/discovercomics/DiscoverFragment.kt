package com.gibsonruitiari.asobi.ui.discovercomics

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
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutDiscoverBinding
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.ItemMarginRecyclerViewDecorator
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.AsobiLogger
import com.gibsonruitiari.asobi.utilities.widgets.ErrorStateLayout
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("UNCHECKED_CAST")
class DiscoverFragment:Fragment() {
    private val discoverViewModel:DiscoverViewModel by viewModel()
    private val logger:AsobiLogger by inject()
    private var isFragmentHidden:Boolean=true
    private var dataLoadingJob:Job?=null
    /* Start of view variables */
    private lateinit var discoverFragmentLoadingLayout:LoadingLayout
    private lateinit var discoverFragmentParentContainer:CoordinatorLayout
    private lateinit var discoverFragmentErrorLayout:ConstraintLayout
    private lateinit var discoverFragmentDataContainer:NestedScrollView
    private lateinit var discoverFragmentAppbarLayout:AppBarLayout
    private lateinit var discoverFragmentCompletedComicsMoreLabel:AppCompatTextView
    private lateinit var discoverFragmentLatestComicsMoreLabel:AppCompatTextView
    private lateinit var discoverFragmentPopularComicsMoreLabel:AppCompatTextView
    private lateinit var discoverFragmentCompletedComicsRecyclerView: RecyclerView
    private lateinit var discoverFragmentLatestComicsRecyclerView: RecyclerView
    private lateinit var discoverFragmentPopularComicsRecyclerView: RecyclerView

    private lateinit var notificationsButton:AppCompatImageButton
    private lateinit var settingsButton:AppCompatImageButton
    /* End of view variables */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState!=null){
            isFragmentHidden=savedInstanceState.getBoolean(isFragmentHiddenTag,true)
        }
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
        val discoverFragmentView = DiscoverFragmentView(requireContext())
        discoverFragmentParentContainer = discoverFragmentView
        discoverFragmentLoadingLayout= discoverFragmentView.loadingLayout
        discoverFragmentErrorLayout=discoverFragmentView.errorLayout
        discoverFragmentDataContainer=discoverFragmentView.parentContainer
        discoverFragmentAppbarLayout=discoverFragmentView.appBarLayout

        discoverFragmentLatestComicsRecyclerView=discoverFragmentView.latestComicsRecyclerView
        discoverFragmentPopularComicsRecyclerView=discoverFragmentView.popularComicsRecyclerView
        discoverFragmentCompletedComicsRecyclerView=discoverFragmentView.completedComicsRecyclerView

        discoverFragmentPopularComicsMoreLabel= discoverFragmentView.popularComicsMoreText
        discoverFragmentLatestComicsMoreLabel=discoverFragmentView.latestComicsMoreText
        discoverFragmentCompletedComicsMoreLabel=discoverFragmentView.completedComicsMoreText

        settingsButton = discoverFragmentView.settingsButton
        notificationsButton =discoverFragmentView.notificationsButton

        discoverFragmentView.parentContainer

        return discoverFragmentView
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
        discoverFragmentParentContainer.postDelayed({discoverFragmentParentContainer.requestApplyInsetsWhenAttached()},500)

    }
    /* Only load data when the fragment comes into view to avoid wastage of resources  */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isFragmentHidden=hidden
        doActionIfWeAreOnDebug { logger.i("is discover fragment hidden $hidden")}
        when{
            !isFragmentHidden->{
                observeStateFromViewModel()
            }
            else->dataLoadingJob.cancelIfActive()
        }
    }

    private fun observeStateFromViewModel(){
       dataLoadingJob?.cancel()
      dataLoadingJob=launchAndRepeatWithViewLifecycle {
        observeData()
        observeSideEffects()
    }
}
    private  fun CoroutineScope.observeData(){
        launch {
            discoverViewModel.observeState().collectLatest {
                when{
                    it.isLoading->{
                        onDataLoadingShowLoadingLayout()
                        discoverFragmentParentContainer.showSnackBar(getString(R.string.loading_msg))
                    }
                    else->{
                            when{
                                it.isDataEmpty() ->{
                                    errorTitle.text=getString(R.string.error_state_title)
                                    errorSubtitle.text=getString(R.string.empty_subtitle)
                                    onErrorOrEmptyDataShowErrorEmptyLayout()
                                }
                                else->{
                                    it.submitDataRecyclerViewAdapterWhenItIsNotEmpty()
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
                     discoverFragmentParentContainer.showSnackBar(errorMessage)
                    onErrorOrEmptyDataShowErrorEmptyLayout()
                 }
             }
         }
        }
    }


     /* Start: Set up ui components */
    /* Navigate to notifications activity/settings activity when the buttons are clicked */
    private fun setUpDiscoverFragmentToolbarButtonsOnClickListener(){
    notificationsButton.setOnClickListener { doActionIfWeAreOnDebug {discoverFragmentParentContainer.showSnackBar("notifications"); logger.i("notifications button clicked") } }
    settingsButton.setOnClickListener { doActionIfWeAreOnDebug {discoverFragmentParentContainer.showSnackBar("settings"); logger.i("settings button clicked") } }
    }
    private fun setUpDiscoverFragmentRecyclerViews(){
     val linearSnapHelper = LinearSnapHelper()
        with(discoverFragmentLatestComicsRecyclerView){
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
        // bottom padding
        doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
            val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = viewPaddingState.bottom+ systemInsets.bottom + resources.getDimension(R.dimen.default_padding).toInt())}
        }
    with(discoverFragmentPopularComicsRecyclerView){
        linearSnapHelper.attachToRecyclerView(this)
        layoutManager = horizontalLayoutManager()
        adapter = popularComicsAdapter
        addItemDecoration(ItemMarginRecyclerViewDecorator(resources.getDimension(R.dimen.default_padding).toInt()))
        setHasFixedSize(true)
    }
    }
    /* Navigate to the specific fragment when more button is clicked */
    private fun onMoreLabelClickListeners(){
    discoverFragmentPopularComicsMoreLabel.setOnClickListener { doActionIfWeAreOnDebug { discoverFragmentParentContainer.showSnackBar("popular comics label clicked");logger.i("popular comics label clicked") } }
    discoverFragmentCompletedComicsMoreLabel.setOnClickListener { doActionIfWeAreOnDebug { discoverFragmentParentContainer.showSnackBar("completed comics label clicked");logger.i("completed comics label clicked") } }
    discoverFragmentLatestComicsMoreLabel.setOnClickListener { doActionIfWeAreOnDebug { discoverFragmentParentContainer.showSnackBar("latest comics label clicked");logger.i("latest comics label clicked") } }
    }
    /* End: Set up ui components */



   /* Start: Respond to events by showing the requisite state on the screen to the user */
    private fun onDataLoadingShowLoadingLayout(){
        discoverFragmentLoadingLayout.apply { visibility=View.VISIBLE }.show()
       discoverFragmentDataContainer.visibility =View.GONE
       discoverFragmentAppbarLayout.visibility=View.GONE
       discoverFragmentErrorLayout.visibility =View.GONE
    }
    private fun onErrorOrEmptyDataShowErrorEmptyLayout(){
        discoverFragmentDataContainer.visibility =View.GONE
        discoverFragmentAppbarLayout.visibility=View.GONE
        discoverFragmentLoadingLayout.apply { visibility=View.GONE }.hide()
        discoverFragmentErrorLayout.visibility=View.VISIBLE
    }
    private fun onDataLoadedSuccessfullyShowDataLayout(){
        discoverFragmentAppbarLayout.visibility=View.GONE
        discoverFragmentLoadingLayout.apply { visibility=View.GONE }.hide()
        discoverFragmentErrorLayout.visibility=View.GONE
        discoverFragmentDataContainer.visibility =View.VISIBLE
    }

    /* End: Respond to events by showing the requisite state on the screen to the user */

    /* Start: Utility functions related to DiscoverFragment  */

    val errorTitle:AppCompatTextView
    get() =(discoverFragmentErrorLayout as ErrorStateLayout).errorTitle
    val errorSubtitle:AppCompatTextView
    get() =(discoverFragmentErrorLayout as ErrorStateLayout).subtitleError

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
    companion object{
        private const val isFragmentHiddenTag ="discoverIsFragmentHidden"
    }
}
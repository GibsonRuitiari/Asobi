package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.animation.LayoutTransition
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.databinding.ComicsByGenreFragmentBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.MainNavigationFragment
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.composedPagedAdapter
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.UiMeasureSpec
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.ExtendedFabBehavior
import com.gibsonruitiari.asobi.utilities.RecyclerViewItemDecoration
import com.gibsonruitiari.asobi.utilities.StatusBarScrimBehavior
import com.gibsonruitiari.asobi.utilities.convertToPxFromDp
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("UNCHECKED_CAST")
class ComicsByGenreFragment: MainNavigationFragment() {
    private var _comicsByGenreBinding:ComicsByGenreFragmentBinding?=null
    private val comicsByGenreBinding:ComicsByGenreFragmentBinding get() = _comicsByGenreBinding!!
    private val mainActivityViewModel:MainActivityViewModel by viewModel()
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel()

    /* Start of view variables  */
    private lateinit var mainFragmentSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mainFragmentRecyclerView:RecyclerView
    private lateinit var mainFragmentExtendedFabActionButton:ExtendedFloatingActionButton
    private lateinit var mainFragmentConstraintLayoutContainer:ConstraintLayout
    private lateinit var mainFragmentFrameLayoutContainer:FrameLayout
    private lateinit var loadingLayout: LoadingLayout
    private lateinit var mainFragmentError_EmptyLayoutContainer:ConstraintLayout
    private lateinit var mainFragmentError_EmptyLayoutImageView:AppCompatImageView
    private lateinit var mainFragmentError_EmptySubtitle:AppCompatTextView
    private lateinit var mainFragmentError_EmptyTitle:AppCompatTextView
    private lateinit var mainFragmentRetryButton:MaterialButton
    /* End of view variables  */

    private val comicsByGenreAdapter =  composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
        viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener { onComicClicked(item) }
        }
    }, bindViewHolder = { viewHolder: RecyclerView.ViewHolder, item: ViewComics?, _ ->
        (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
    })
    private var BindingViewHolder<ComicItemLayoutBinding>.item by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(viewComics: ViewComics?){
        viewComics?.let {comic->
            this.item = comic
            with(binding){
                comicsImageView.loadPhotoUrl(comic.comicThumbnail)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val colorSchemes=resourcesInstance().getIntArray(R.array.swipe_refresh_colors)
        _comicsByGenreBinding = ComicsByGenreFragmentBinding.inflate(inflater,container,false)
        val parentContainer = comicsByGenreBinding.root
        val appBarScrimVew = View(parentContainer.context).apply {
            layoutParams= CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
            background = requireActivity().resources.getDrawable(R.color.color_surface,null)
            fitsSystemWindows= true
            val parentLayoutParams=layoutParams as CoordinatorLayout.LayoutParams
            parentLayoutParams.behavior= StatusBarScrimBehavior(parentContainer.context)
        }
        parentContainer.addView(appBarScrimVew)

        /* Add extended floating button */
        mainFragmentExtendedFabActionButton = ExtendedFloatingActionButton(parentContainer.context).apply {
            id = ViewCompat.generateViewId()
            text = resourcesInstance().getText(R.string.filter)
            icon = resourcesInstance().getDrawable(R.drawable.ic_baseline_filter_list_24, null)
            contentDescription=resourcesInstance().getString(R.string.filter_comics_by_genre)
            layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            (layoutParams as CoordinatorLayout.LayoutParams).setMargins(resourcesInstance().getDimension(R.dimen.keyline_7).toInt(),
                resourcesInstance().getDimension(R.dimen.keyline_7).toInt(),
                resourcesInstance().getDimension(R.dimen.keyline_7).toInt(),
                resourcesInstance().getDimension(R.dimen.keyline_7).toInt())
            (layoutParams as CoordinatorLayout.LayoutParams).gravity =Gravity.BOTTOM+Gravity.END
            (layoutParams as CoordinatorLayout.LayoutParams).behavior= ExtendedFabBehavior(parentContainer.context)
        }

        parentContainer.addView(mainFragmentExtendedFabActionButton)

        /* Add  constraint layout  container*/

        mainFragmentConstraintLayoutContainer = ConstraintLayout(parentContainer.context).apply {
            id = ViewCompat.generateViewId()
            layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
        }
        parentContainer.addView(mainFragmentConstraintLayoutContainer)

        /* Add swipe refresh layout */
        mainFragmentSwipeRefreshLayout = SwipeRefreshLayout(mainFragmentConstraintLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            setOnRefreshListener { comicsByGenreAdapter.refresh() }
            doOnNextLayout { setContentToMaxWidth(this) }
            setColorSchemeColors(*colorSchemes)
        }
        mainFragmentConstraintLayoutContainer.addView(mainFragmentSwipeRefreshLayout)
        val set= ConstraintSet()
        set.clone(mainFragmentConstraintLayoutContainer)
        /* set constraints for swipe refresh layout*/
        set.constrainWidth(mainFragmentSwipeRefreshLayout.id,resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt())
        set.constrainHeight(mainFragmentSwipeRefreshLayout.id,resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt()) // spread as far as possible
        set.connect(mainFragmentSwipeRefreshLayout.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(mainFragmentSwipeRefreshLayout.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(mainFragmentSwipeRefreshLayout.id,ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(mainFragmentSwipeRefreshLayout.id,ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.applyTo(mainFragmentConstraintLayoutContainer)


        /* add frame layout to swipe refresh layout*/
        mainFragmentFrameLayoutContainer = FrameLayout(mainFragmentSwipeRefreshLayout.context).apply {
            id= ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
            /* animate layout changes using the default LayoutTransition() */
            layoutTransition= LayoutTransition()
        }
        mainFragmentSwipeRefreshLayout.addView(mainFragmentFrameLayoutContainer)

        /* Stack things up on the frame layout container-> recycler view;error-layout;empty-layout;loading-layout */

        /* Loading layout initially the view is visible */
        loadingLayout = LoadingLayout(mainFragmentFrameLayoutContainer.context).apply {
            id=ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }
        mainFragmentFrameLayoutContainer.addView(loadingLayout)

        /* Recycler View that holds the paginated data initially is it invisible/gone */
        mainFragmentRecyclerView = RecyclerView(mainFragmentFrameLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
            val animation=AnimationUtils.loadLayoutAnimation(this.context, R.anim.layout_animation_scale_in)
            layoutAnimation= animation
            adapter = comicsByGenreAdapter
            setHasFixedSize(true)
            visibility = View.GONE
        }
        mainFragmentFrameLayoutContainer.addView(mainFragmentRecyclerView)


        /* The Error Empty Layout (for lack of a better word) basically the layout that will be shown in-case data is empty
        * or there is an error while loading the data from network  */

        mainFragmentError_EmptyLayoutContainer = ConstraintLayout(mainFragmentFrameLayoutContainer.context).apply {
            id= ViewCompat.generateViewId()
            layoutParams=FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
            background = resourcesInstance().getDrawable(R.color.color_surface,null)
            visibility = View.GONE
        }

        mainFragmentFrameLayoutContainer.addView(mainFragmentError_EmptyLayoutContainer)

        /* Add things to the Error_EmptyLayout Container -> image to be shown to indicate error
        * title and subtitle to show the user */

        mainFragmentError_EmptyLayoutImageView = AppCompatImageView(mainFragmentError_EmptyLayoutContainer.context).apply{
            id= ViewCompat.generateViewId()
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource( R.drawable.no_internet_connection_image)
            contentDescription= getString(R.string.error_image)
        }

        mainFragmentError_EmptyLayoutContainer.addView(mainFragmentError_EmptyLayoutImageView)


         mainFragmentError_EmptyTitle = AppCompatTextView(mainFragmentError_EmptyLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            gravity= Gravity.CENTER
            textSize = resourcesInstance().getDimension(R.dimen.error_empty_title_size)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.SANS_SERIF

        }
        mainFragmentError_EmptyLayoutContainer.addView(mainFragmentError_EmptyTitle)

         mainFragmentError_EmptySubtitle = AppCompatTextView(mainFragmentError_EmptyLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            gravity=Gravity.CENTER
            textSize =   resourcesInstance().getDimension(R.dimen.error_empty_subtitle_size)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            text="Try searching for something"
        }

        mainFragmentError_EmptyLayoutContainer.addView(mainFragmentError_EmptySubtitle)

        mainFragmentRetryButton = MaterialButton(mainFragmentError_EmptyLayoutContainer.context).apply{
            id = ViewCompat.generateViewId()
            gravity = Gravity.CENTER
            text=getString(R.string.cd_retry)
            textSize= resourcesInstance().getDimension(R.dimen.error_empty_subtitle_size)
            textAlignment=View.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.SANS_SERIF
            visibility=View.GONE

        }
        mainFragmentError_EmptyLayoutContainer.addView(mainFragmentRetryButton)

        /* Apply constraints to emptyErrorLayoutContainer together with it's children */

        val errorEmptyLayoutConstraintSet = ConstraintSet()
        errorEmptyLayoutConstraintSet.clone(mainFragmentError_EmptyLayoutContainer)

        /*Set the width and height of the error_empty layout container's children views  */
        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentError_EmptyTitle.id, resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt())
        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentError_EmptyTitle.id,ConstraintSet.WRAP_CONTENT)

        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentError_EmptySubtitle.id, resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt())
        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentError_EmptySubtitle.id, ConstraintSet.WRAP_CONTENT)


        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentError_EmptyLayoutImageView.id,resourcesInstance().getDimension(R.dimen.comic_item_width).toInt())
        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentError_EmptyLayoutImageView.id,resourcesInstance().getDimension(R.dimen.comic_item_width).toInt())

        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentRetryButton.id,resourcesInstance().getDimension(R.dimen.retry_button_width_dimen).toInt())
        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentRetryButton.id,resourcesInstance().getDimension(R.dimen.retry_button_height_dimen).toInt())


        /* Set the top margin for one of the error_empty layout container's children views */

        errorEmptyLayoutConstraintSet.setMargin(mainFragmentError_EmptySubtitle.id,ConstraintSet.TOP,resourcesInstance().getDimension(R.dimen.keyline_8).toInt())
        errorEmptyLayoutConstraintSet.setMargin(mainFragmentRetryButton.id, ConstraintSet.TOP, resourcesInstance().getDimension(R.dimen.keyline_7).toInt())

        /* Set the constraints for error_empty layout container's children views */

        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyLayoutImageView.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyLayoutImageView.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyLayoutImageView.id,ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyLayoutImageView.id,ConstraintSet.BOTTOM, mainFragmentError_EmptyTitle.id, ConstraintSet.TOP)

        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptySubtitle.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptySubtitle.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptySubtitle.id,ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptySubtitle.id,ConstraintSet.TOP,mainFragmentError_EmptyTitle.id, ConstraintSet.BOTTOM)

        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyTitle.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyTitle.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyTitle.id, ConstraintSet.TOP,mainFragmentError_EmptyLayoutImageView.id, ConstraintSet.BOTTOM)
        errorEmptyLayoutConstraintSet.connect(mainFragmentError_EmptyTitle.id, ConstraintSet.BOTTOM,mainFragmentError_EmptySubtitle.id, ConstraintSet.TOP)

        errorEmptyLayoutConstraintSet.connect(mainFragmentRetryButton.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentRetryButton.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentRetryButton.id,ConstraintSet.TOP, mainFragmentError_EmptySubtitle.id, ConstraintSet.BOTTOM)

        /* Apply the constraints to EmptyError Layout container */
        errorEmptyLayoutConstraintSet.applyTo(mainFragmentError_EmptyLayoutContainer)


        return parentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragmentExtendedFabActionButton.applyBottomInsets()
        listenToUiEventsAndUpdateUiAccordingly()

        /* Listen to/collect data in this lifecycle scope  */
        launchAndRepeatWithViewLifecycle {
            launch { /* Observe paged data */  observePagedDataAndSubmitItToRecyclerView()}
            launch { /*Observe screen size data */  observeScreenWidthState()}
            launch { /*Observe uiMeasureSpec and construct recycler view accordingly */ observeScreenMeasureSpecState() }
        }
    }
    /* Observe data start */
    private suspend fun observePagedDataAndSubmitItToRecyclerView(){
        comicsByGenreViewModel.comicsList.collectLatest {
            comicsByGenreAdapter.submitData(it)
        }
    }
    private suspend fun observeScreenWidthState(){
        mainActivityViewModel.screenWidthState.collectLatest {
            mainActivityViewModel.setUiMeasureSpec((it.constructUiMeasureSpecFromScreenSize()))
        }
    }
    private suspend fun observeScreenMeasureSpecState(){
        mainActivityViewModel.uiMeasureSpecState.collectLatest {
            setUpMainFragmentRecyclerView(it)
        }
    }
    /* Observe data end */

    /*Start:Show Correct State based on the data events observed above */
    private fun onDataLoadedSuccessfullyShowData(){
        mainFragmentRecyclerView.isVisible = true
        mainFragmentError_EmptyLayoutContainer.isVisible = false
        loadingLayout.hide()
    }

    private fun onErrorOrEmptyDataShowErrorOrEmptyState(){
        mainFragmentRecyclerView.isVisible=false
        mainFragmentError_EmptyLayoutContainer.isVisible=true
        loadingLayout.hide()
    }
    private fun onLoadingShowLoadingState(){
        mainFragmentRecyclerView.isVisible=false
        mainFragmentError_EmptyLayoutContainer.isVisible=false
        loadingLayout.show()
    }

    /*End: Observe Ui States And Show Correct State */
    private fun listenToUiEventsAndUpdateUiAccordingly(){
        comicsByGenreAdapter.addLoadStateListener {
            when(it.refresh){
                is LoadState.NotLoading->{
                    if (comicsByGenreAdapter.itemCount ==0){
                        setEmpty_ErrorStateTitleAndSubtitle(getString(R.string.error_state_title), getString(R.string.empty_title))
                        onErrorOrEmptyDataShowErrorOrEmptyState()
                        onDataLoadingFailureShowRetryButtonAndSetUpRetryAction()
                    }else{
                        onDataLoadedSuccessfullyShowData()
                    }
                }
                is LoadState.Loading-> onLoadingShowLoadingState()
                is LoadState.Error->{
                    val throwable_ = (it.refresh as LoadState.Error).error
                    val errorMessage = when(throwable_){
                        is UnknownHostException, is SocketTimeoutException, is ConnectException->{
                            getString(R.string.network_error_msg)
                        }
                        else-> "Loading of data failed due: ${throwable_.message} ${System.lineSeparator()}Please try again later"
                    }
                    mainFragmentFrameLayoutContainer.showSnackBar(errorMessage)
                    setEmpty_ErrorStateTitleAndSubtitle(getString(R.string.error_state_title),
                    errorMessage)
                    // retry perphaps
                    onDataLoadingFailureShowRetryButtonAndSetUpRetryAction()
                    onErrorOrEmptyDataShowErrorOrEmptyState()
                }
            }
            setUpSwipeRefreshWidgetState(mainFragmentSwipeRefreshLayout.isRefreshing && (it.refresh is LoadState.Loading))
        }

    }
    private fun onDataLoadingFailureShowRetryButtonAndSetUpRetryAction(){
        mainFragmentRetryButton.isVisible=true
        mainFragmentRetryButton.setOnClickListener {
            comicsByGenreAdapter.retry()
        }
    }

    private fun onComicClicked(comicItem: ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private fun setUpSwipeRefreshWidgetState(isRefreshing:Boolean){
        mainFragmentSwipeRefreshLayout.isRefreshing = isRefreshing
    }
    private fun setEmpty_ErrorStateTitleAndSubtitle(title:String,subtitle:String){
        mainFragmentError_EmptyTitle.text = title
        mainFragmentError_EmptySubtitle.text = subtitle
    }
    private fun setUpMainFragmentRecyclerView(uiMeasureSpec: UiMeasureSpec){
        val spanCount = uiMeasureSpec.recyclerViewColumns
        with(mainFragmentRecyclerView){
            applyBottomInsets()
            scrollToTop()
            layoutManager = gridLayoutManager(spanCount = spanCount)
            addItemDecoration(RecyclerViewItemDecoration(spanCount,
            includeEdge = true, spacing = requireActivity().convertToPxFromDp(uiMeasureSpec.recyclerViewMargin)))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _comicsByGenreBinding = null
    }


}
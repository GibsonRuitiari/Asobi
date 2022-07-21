package com.gibsonruitiari.asobi.ui

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
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gibsonruitiari.asobi.BuildConfig
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.BaseFragmentBinding
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.composedPagedAdapter
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.ExtendedFabBehavior
import com.gibsonruitiari.asobi.utilities.StatusBarScrimBehavior
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("UNCHECKED_CAST")
abstract class MainFragment:MainNavigationFragment(){
    private var _baseFragmentBinding: BaseFragmentBinding?=null
    private val fragmentBinding get() = _baseFragmentBinding!!
    var pagingListAdapter:PagingDataAdapter<ViewComics,RecyclerView.ViewHolder> ?=null
    abstract val toolbarTitle:String
    abstract suspend fun observePagedData()
    abstract fun onComicClicked(comicItem: ViewComics)


    /* Start of view variables  */
    private lateinit var mainFragmentSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mainFragmentRecyclerView:RecyclerView
    private lateinit var mainFragmentExtendedFabActionButton:ExtendedFloatingActionButton
    private lateinit var mainFragmentConstraintLayoutContainer:ConstraintLayout
    private lateinit var mainFragmentFrameLayoutContainer:FrameLayout
    private lateinit var loadingLayout: LoadingLayout
    private lateinit var mainFragmentErrorEmptyLayoutContainer:ConstraintLayout
    private lateinit var mainFragmentErrorEmptyLayoutImageView:AppCompatImageView
    private lateinit var mainFragmentErrorEmptySubtitle:AppCompatTextView
    private lateinit var mainFragmentErrorEmptyTitle:AppCompatTextView
    private lateinit var mainFragmentRetryButton:MaterialButton
    /* End of view variables  */


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
    ): View? {
        _baseFragmentBinding = BaseFragmentBinding.inflate(inflater,container,false)
        val colorSchemes=resourcesInstance().getIntArray(R.array.swipe_refresh_colors)
        val parentContainer = fragmentBinding.root
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
            (layoutParams as CoordinatorLayout.LayoutParams).setMargins(resourcesInstance().getDimension(R.dimen.keyline_7).toInt(),resourcesInstance().getDimension(R.dimen.keyline_7).toInt(),resourcesInstance().getDimension(R.dimen.keyline_7).toInt(),resourcesInstance().getDimension(R.dimen.keyline_7).toInt())
            (layoutParams as CoordinatorLayout.LayoutParams).gravity = Gravity.BOTTOM+ Gravity.END
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


            setColorSchemeColors(*colorSchemes)
        }
        mainFragmentConstraintLayoutContainer.addView(mainFragmentSwipeRefreshLayout)
        val set= ConstraintSet()
        set.clone(mainFragmentConstraintLayoutContainer)
        /* set constraints for swipe refresh layout*/
        set.constrainWidth(mainFragmentSwipeRefreshLayout.id,resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt())
        set.constrainHeight(mainFragmentSwipeRefreshLayout.id,resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt()) // spread as far as possible
        set.connect(mainFragmentSwipeRefreshLayout.id,
            ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(mainFragmentSwipeRefreshLayout.id,
            ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(mainFragmentSwipeRefreshLayout.id,
            ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(mainFragmentSwipeRefreshLayout.id,
            ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
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
            id= ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }
        mainFragmentFrameLayoutContainer.addView(loadingLayout)

        /* Recycler View that holds the paginated data initially is it invisible/gone */
        mainFragmentRecyclerView = RecyclerView(mainFragmentFrameLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            val animation= AnimationUtils.loadLayoutAnimation(this.context, R.anim.layout_animation_scale_in)
            layoutAnimation= animation

            visibility = View.GONE
        }
        mainFragmentFrameLayoutContainer.addView(mainFragmentRecyclerView)


        /* The Error Empty Layout (for lack of a better word) basically the layout that will be shown in-case data is empty
        * or there is an error while loading the data from network  */

        mainFragmentErrorEmptyLayoutContainer = ConstraintLayout(mainFragmentFrameLayoutContainer.context).apply {
            id= ViewCompat.generateViewId()
            layoutParams= FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
            background = resourcesInstance().getDrawable(R.color.color_surface,null)
            visibility = View.GONE
        }

        mainFragmentFrameLayoutContainer.addView(mainFragmentErrorEmptyLayoutContainer)

        /* Add things to the Error_EmptyLayout Container -> image to be shown to indicate error
        * title and subtitle to show the user */

        mainFragmentErrorEmptyLayoutImageView = AppCompatImageView(mainFragmentErrorEmptyLayoutContainer.context).apply{
            id= ViewCompat.generateViewId()
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource( R.drawable.no_internet_connection_image)
            contentDescription= getString(R.string.error_image)
        }

        mainFragmentErrorEmptyLayoutContainer.addView(mainFragmentErrorEmptyLayoutImageView)


        mainFragmentErrorEmptyTitle = AppCompatTextView(mainFragmentErrorEmptyLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            gravity= Gravity.CENTER
            textSize = 16f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.SANS_SERIF

        }
        mainFragmentErrorEmptyLayoutContainer.addView(mainFragmentErrorEmptyTitle)

        mainFragmentErrorEmptySubtitle = AppCompatTextView(mainFragmentErrorEmptyLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            gravity= Gravity.CENTER
            textSize = 14f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            text="Try searching for something"
        }

        mainFragmentErrorEmptyLayoutContainer.addView(mainFragmentErrorEmptySubtitle)

        mainFragmentRetryButton = MaterialButton(mainFragmentErrorEmptyLayoutContainer.context).apply{
            id = ViewCompat.generateViewId()
            gravity = Gravity.CENTER
            text=getString(R.string.cd_retry)
            textSize= 14f
            textAlignment=View.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.SANS_SERIF
            visibility=View.GONE

        }
        mainFragmentErrorEmptyLayoutContainer.addView(mainFragmentRetryButton)

        /* Apply constraints to emptyErrorLayoutContainer together with it's children */

        val errorEmptyLayoutConstraintSet = ConstraintSet()
        errorEmptyLayoutConstraintSet.clone(mainFragmentErrorEmptyLayoutContainer)

        /*Set the width and height of the error_empty layout container's children views  */
        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentErrorEmptyTitle.id, resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt())
        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentErrorEmptyTitle.id, ConstraintSet.WRAP_CONTENT)

        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentErrorEmptySubtitle.id, resourcesInstance().getDimension(R.dimen.match_constraint_value).toInt())
        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentErrorEmptySubtitle.id, ConstraintSet.WRAP_CONTENT)


        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentErrorEmptyLayoutImageView.id,resourcesInstance().getDimension(R.dimen.comic_item_width).toInt())
        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentErrorEmptyLayoutImageView.id,resourcesInstance().getDimension(R.dimen.comic_item_width).toInt())

        errorEmptyLayoutConstraintSet.constrainWidth(mainFragmentRetryButton.id,resourcesInstance().getDimension(R.dimen.retry_button_width_dimen).toInt())
        errorEmptyLayoutConstraintSet.constrainHeight(mainFragmentRetryButton.id,resourcesInstance().getDimension(R.dimen.retry_button_height_dimen).toInt())


        /* Set the top margin for one of the error_empty layout container's children views */


        errorEmptyLayoutConstraintSet.setMargin(mainFragmentRetryButton.id, ConstraintSet.TOP,10)

        /* Set the constraints for error_empty layout container's children views */

        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyLayoutImageView.id,
            ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyLayoutImageView.id,
            ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyLayoutImageView.id,
            ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyLayoutImageView.id,
            ConstraintSet.BOTTOM, mainFragmentErrorEmptyTitle.id, ConstraintSet.TOP)

        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptySubtitle.id,
            ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptySubtitle.id,
            ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptySubtitle.id,
            ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptySubtitle.id,
            ConstraintSet.TOP,mainFragmentErrorEmptyTitle.id, ConstraintSet.BOTTOM)

        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyTitle.id,
            ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyTitle.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyTitle.id, ConstraintSet.TOP,mainFragmentErrorEmptyLayoutImageView.id, ConstraintSet.BOTTOM)
        errorEmptyLayoutConstraintSet.connect(mainFragmentErrorEmptyTitle.id, ConstraintSet.BOTTOM,mainFragmentErrorEmptySubtitle.id, ConstraintSet.TOP)

        errorEmptyLayoutConstraintSet.connect(mainFragmentRetryButton.id,
            ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(mainFragmentRetryButton.id,
            ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(mainFragmentRetryButton.id,
            ConstraintSet.TOP, mainFragmentErrorEmptySubtitle.id, ConstraintSet.BOTTOM)

        /* Apply the constraints to EmptyError Layout container */
        errorEmptyLayoutConstraintSet.applyTo(mainFragmentErrorEmptyLayoutContainer)


        return parentContainer

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagingListAdapter = setUpRecyclerViewAdapter()
        mainFragmentExtendedFabActionButton.applyBottomInsets()
        setUpMainFragmentRecyclerView()
        listenToUiEventsAndUpdateUiAccordingly()
        setUpSwipeRefreshWidget()
        /*Perform collection of multiple flows here  */
        launchAndRepeatWithViewLifecycle {
            launch {  /* Observe paged data */ observePagedData() }

        }
    }

    /*Start:Show Correct State based on the data events observed above */
    private fun onDataLoadedSuccessfullyShowData(){
        mainFragmentRecyclerView.isVisible = true
        mainFragmentErrorEmptyLayoutContainer.isVisible = false
        loadingLayout.hide()
    }

    private fun onErrorOrEmptyDataShowErrorOrEmptyState(){
        mainFragmentRecyclerView.isVisible=false
        mainFragmentErrorEmptyLayoutContainer.isVisible=true
        loadingLayout.hide()
    }
    private fun onLoadingShowLoadingState(){
        mainFragmentRecyclerView.isVisible=false
        mainFragmentErrorEmptyLayoutContainer.isVisible=false
        loadingLayout.show()
    }

    /*End: Observe Ui States And Show Correct State */
    private fun listenToUiEventsAndUpdateUiAccordingly(){
        pagingListAdapter?.addLoadStateListener {
            when(it.refresh){
                is LoadState.NotLoading->{
                    if (pagingListAdapter?.itemCount ==0){
                        setEmptyErrorStateTitleAndSubtitle(getString(R.string.error_state_title), getString(R.string.empty_title))
                        onErrorOrEmptyDataShowErrorOrEmptyState()
                        onDataLoadingFailureShowRetryButtonAndSetUpRetryAction()
                    }else{
                        onDataLoadedSuccessfullyShowData()
                    }
                }
                is LoadState.Loading-> onLoadingShowLoadingState()
                is LoadState.Error->{
                    val errorMessage = when(val throwable_ = (it.refresh as LoadState.Error).error){
                        is UnknownHostException, is SocketTimeoutException, is ConnectException->{
                            getString(R.string.network_error_msg)
                        }
                        else-> "Loading of data failed due: ${throwable_.message} ${System.lineSeparator()}Please try again later"
                    }
                    mainFragmentFrameLayoutContainer.showSnackBar(errorMessage)
                    setEmptyErrorStateTitleAndSubtitle(getString(R.string.error_state_title),
                        errorMessage)
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
            pagingListAdapter?.retry()
        }
    }
    /* Start: Setting up Ui Components */
    private fun setEmptyErrorStateTitleAndSubtitle(title:String, subtitle:String){
        mainFragmentErrorEmptyTitle.text = title
        mainFragmentErrorEmptySubtitle.text = subtitle
    }


    private fun setUpRecyclerViewAdapter():PagingDataAdapter<ViewComics,RecyclerView.ViewHolder> = composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
        viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener { onComicClicked(item) }
        }
    }, bindViewHolder = { viewHolder: RecyclerView.ViewHolder, item: ViewComics?, _ ->
        (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
    })

    private fun setUpSwipeRefreshWidgetState(isRefreshing:Boolean){
        mainFragmentSwipeRefreshLayout.isRefreshing = isRefreshing
    }
    private fun setUpSwipeRefreshWidget(){
        with(mainFragmentSwipeRefreshLayout){
            doOnNextLayout {
                // similar to Modifier.maxWidth() in compose
                setContentToMaxWidth(mainFragmentSwipeRefreshLayout)
            }
            setOnRefreshListener { pagingListAdapter?.refresh() }

            val ct=WindowInsetsCompat.Type.displayCutout()

            val height = if (ct!=0){
                ct +height
            }else 0
            setSlingshotDistance(128+height)
            setProgressViewEndTarget(false, height+128)
        }
    }
    private fun setUpMainFragmentRecyclerView(){
        val screenWidth= resourcesInstance().displayMetrics.run {
            if (BuildConfig.DEBUG){
                println("widthpixels: $widthPixels density $density")
            }
            widthPixels/density }
        with(mainFragmentRecyclerView){
            doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type
                        .ime())
                view.updatePadding(bottom= viewPaddingState.bottom + systemInsets.bottom, top= viewPaddingState.top + systemInsets.top)
            }
            setHasFixedSize(true)
            scrollToTop()
            adapter = pagingListAdapter
            /*By default the medium density is 160f so we minus 4 just increase to accommodate smaller screens and come up with a proper
            * no of span count for our grid layout */
            layoutManager = gridLayoutManager(spanCount = (screenWidth/156f).toInt())
        }
    }
    /* End: Setting up Ui Components */
    override fun onDestroy() {
        super.onDestroy()
        _baseFragmentBinding = null
        pagingListAdapter =null
    }
}
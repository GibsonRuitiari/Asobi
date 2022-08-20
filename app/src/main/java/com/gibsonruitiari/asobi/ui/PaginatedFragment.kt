package com.gibsonruitiari.asobi.ui

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.composedPagedAdapter
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@Suppress("UNCHECKED_CAST")
abstract class PaginatedFragment:Fragment(){

    private var isFragmentHidden:Boolean=true
    private var loadingJob:Job?=null
    private val logger:Logger by inject()
    private var pagingDataAdapter:PagingDataAdapter<ViewComics,RecyclerView.ViewHolder> ?=null
    val listAdapter:PagingDataAdapter<ViewComics,RecyclerView.ViewHolder> get() = pagingDataAdapter!!

    abstract fun getTitle():String
    abstract fun getFragmentColor():Int
    abstract suspend fun observePagedData()
    abstract fun onComicClicked(comicItem: ViewComics)


    /* Start of view variables  */
    private lateinit var paginatedFragmentSwipeRefreshLayout:SwipeRefreshLayout
     lateinit var paginatedFragmentRecyclerView: RecyclerView
    private lateinit var paginatedFragmentLoadingLayout: LoadingLayout
    private lateinit var paginatedFragmentErrorEmptyLayout:ConstraintLayout
    private lateinit var paginatedFragmentAppBarLayout:AppBarLayout
    private lateinit var paginatedFragmentToolbar:Toolbar
    private lateinit var paginatedFragmentConstraintLayout:ConstraintLayout
    private lateinit var paginatedFragmentErrorEmptyTitle:AppCompatTextView
    private lateinit var paginatedFragmentErrorEmptySubtitle:AppCompatTextView
    private lateinit var paginatedFragmentViewErrorEmptyRetryButton:MaterialButton

    /* End of view variables  */

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState==null) return
        isFragmentHidden=savedInstanceState.getBoolean(isFragmentHiddenTag,true)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFragmentHiddenTag,isFragmentHidden)
    }

    private var BindingViewHolder<ComicItemLayoutBinding>.item by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(viewComics: ViewComics?){
        viewComics?.let {comic->
            this.item = comic
            with(binding){
                comicsImageView.loadPhotoUrl(comic.comicThumbnail)
                title.text=item.comicName
                title.visibility=View.VISIBLE
            }
        }
    }
    inner class PaginatedFragmentView constructor(context: Context)
        :ParentFragmentsView(context){
        lateinit var paginatedFragmentAppBarLayout:AppBarLayout
        lateinit var paginatedFragmentToolbar:Toolbar
        lateinit var paginatedFragmentConstraintLayout:ConstraintLayout
        lateinit var paginatedFragmentSwipeRefreshLayout: SwipeRefreshLayout
        lateinit var paginatedFragmentRecyclerView: RecyclerView
        private val colorSchemes=resourcesInstance().getIntArray(R.array.swipe_refresh_colors)
        init {
            paginatedFragmentAppBarLayout(context)
            paginatedFragmentConstraintLayout(context)
        }
        private fun paginatedFragmentAppBarLayout(context: Context){
            paginatedFragmentAppBarLayout = AppBarLayout(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                (layoutParams as LayoutParams).setMargins(0.dp)
                setBackgroundColor(resources.getColor(R.color.transparent,null))
                fitsSystemWindows=true
            }
            addView(paginatedFragmentAppBarLayout)
            paginatedFragmentToolbar = paginatedFragmentMaterialToolbar(paginatedFragmentAppBarLayout.context)
            paginatedFragmentAppBarLayout.addView(paginatedFragmentToolbar)

        }
        private fun paginatedFragmentMaterialToolbar(context: Context):Toolbar{
            val materialToolbar = Toolbar(context).apply{
                id=ViewCompat.generateViewId()
                layoutParams = AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    65.dp)
                setBackgroundColor(resources.getColor(R.color.transparent,null))
                elevation=0f
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags= AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL + AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            }
            return materialToolbar
        }
        private fun paginatedFragmentConstraintLayout(context: Context){
            paginatedFragmentConstraintLayout=ConstraintLayout(context).apply {
                id=ViewCompat.generateViewId()
                fitsSystemWindows=true
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                (layoutParams as LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
                (layoutParams as LayoutParams).setMargins(0.dp,10.dp,0.dp,10.dp)
            }
            addView(paginatedFragmentConstraintLayout)
            val constraintSet = ConstraintSet()
            paginatedFragmentSwipeRefreshLayout=paginatedFragmentSwipeRefreshLayout(paginatedFragmentConstraintLayout.context,constraintSet)
            paginatedFragmentConstraintLayout.addView(paginatedFragmentSwipeRefreshLayout)
            constraintSet.applyTo(paginatedFragmentConstraintLayout)
        }
        private fun paginatedFragmentSwipeRefreshLayout(context: Context,constraintSet: ConstraintSet):SwipeRefreshLayout{
            val swipeRefreshLayout = SwipeRefreshLayout(context).apply {
                id = ViewCompat.generateViewId()
                setColorSchemeColors(*colorSchemes)
            }
            val swipeRefreshLayoutId= swipeRefreshLayout.id
            constraintSet.setViewLayoutParams(swipeRefreshLayoutId, ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.MATCH_CONSTRAINT)
            constraintSet constrainTopToParent swipeRefreshLayoutId
            constraintSet constrainEndToParent swipeRefreshLayoutId
            constraintSet constrainStartToParent swipeRefreshLayoutId
            constraintSet constrainBottomToParent  swipeRefreshLayoutId
            paginatedFragmentRecyclerView = paginatedFragmentRecyclerView(swipeRefreshLayout.context)
            swipeRefreshLayout.addView(paginatedFragmentRecyclerView)
            return swipeRefreshLayout
        }
        private fun paginatedFragmentRecyclerView(context: Context):RecyclerView{
            /* Recycler View that holds the paginated data initially is it invisible/gone */
            return RecyclerView(context).apply {
                id = ViewCompat.generateViewId()
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
                val animation= AnimationUtils.loadLayoutAnimation(this.context, R.anim.layout_animation_scale_in)
                animate(animation)
                visibility = View.GONE
            }
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = PaginatedFragmentView(requireContext())
        paginatedFragmentConstraintLayout=fragmentView.paginatedFragmentConstraintLayout
        paginatedFragmentAppBarLayout=fragmentView.paginatedFragmentAppBarLayout
        paginatedFragmentRecyclerView=fragmentView.paginatedFragmentRecyclerView
        paginatedFragmentToolbar=fragmentView.paginatedFragmentToolbar
        paginatedFragmentLoadingLayout=fragmentView.loadingStateLayout
        paginatedFragmentErrorEmptyLayout=fragmentView.errorEmptyStateLayout
        paginatedFragmentSwipeRefreshLayout = fragmentView.paginatedFragmentSwipeRefreshLayout
        paginatedFragmentErrorEmptyTitle = fragmentView.errorTitle
        paginatedFragmentErrorEmptySubtitle = fragmentView.subtitleError
        paginatedFragmentViewErrorEmptyRetryButton= fragmentView.retryButton
        return fragmentView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagingDataAdapter = setUpRecyclerViewAdapter()
        setUpMainFragmentRecyclerView()
        listenToUiEventsAndUpdateUiAccordingly()
        setUpSwipeRefreshWidget()
        animateAppBarColorsOnScroll()
        setUpToolbarTitle()

    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isFragmentHidden=hidden
        when{
            !isFragmentHidden->{
             observeStateFromViewModel()
            }
            else->{
                loadingJob.cancelIfActive()
            }
        }
        changeStatusBarToTransparentInFragment(resources.getColor(R.color.transparent,null))
    }

    private fun applyBarElevationAndBackgroundColor(color:Int,
    barElevation:Float){
        paginatedFragmentAppBarLayout.apply {
            setBackgroundColor(color)
            elevation=barElevation
        }
        paginatedFragmentToolbar.apply {
            setBackgroundColor(color)
            elevation=barElevation
        }
    }
    private fun observeStateFromViewModel(){
        loadingJob?.cancel()
        loadingJob=launchAndRepeatWithViewLifecycle {
            launch {  /* Observe paged data */ observePagedData() }
        }
    }

    /*Start:Show Correct State based on the data events observed above */
    private fun onDataLoadedSuccessfullyShowData(){
        paginatedFragmentAppBarLayout.isVisible=true
        paginatedFragmentRecyclerView.visibility=View.VISIBLE
        paginatedFragmentErrorEmptyLayout.isVisible = false
        paginatedFragmentLoadingLayout.hide()
    }

    private fun onErrorOrEmptyDataShowErrorOrEmptyState(){
        paginatedFragmentRecyclerView.isVisible=false
        paginatedFragmentAppBarLayout.isVisible=false
        paginatedFragmentErrorEmptyLayout.isVisible=true
        paginatedFragmentLoadingLayout.hide()
    }
    private fun onLoadingShowLoadingState(){
        paginatedFragmentRecyclerView.isVisible=false
        paginatedFragmentAppBarLayout.isVisible=false
        paginatedFragmentErrorEmptyLayout.isVisible=false
        paginatedFragmentLoadingLayout.show()
    }

    /*End: Observe Ui States And Show Correct State */
    private fun listenToUiEventsAndUpdateUiAccordingly(){
        pagingDataAdapter?.addLoadStateListener {
            when(it.refresh){
                is LoadState.NotLoading->{
                    if (pagingDataAdapter?.itemCount ==0){
                        setEmptyErrorStateTitleAndSubtitle(getString(R.string.error_state_title), getString(R.string.empty_title))
                        onErrorOrEmptyDataShowErrorOrEmptyState()
                    }else{
                        onDataLoadedSuccessfullyShowData()
                    }
                }
                is LoadState.Loading-> onLoadingShowLoadingState()
                is LoadState.Error->{
                    val throwable_ = (it.refresh as LoadState.Error).error
                    val errorMessage=throwable_.parseThrowableErrorMessageIntoUsefulMessage()

                    paginatedFragmentSwipeRefreshLayout.showSnackBar(errorMessage)
                    setEmptyErrorStateTitleAndSubtitle(getString(R.string.error_state_title),
                        errorMessage)
                    onErrorOrEmptyDataShowErrorOrEmptyState()
                }
            }
            setUpSwipeRefreshWidgetState(paginatedFragmentSwipeRefreshLayout.isRefreshing && (it.refresh is LoadState.Loading))
        }

    }
    /* Start: Setting up Ui Components */
    private fun setEmptyErrorStateTitleAndSubtitle(title:String, subtitle:String){
        paginatedFragmentErrorEmptyTitle.text=title
        paginatedFragmentErrorEmptySubtitle.text = subtitle
    }
    private fun setUpToolbarTitle(){
        paginatedFragmentToolbar.title= getTitle()
        paginatedFragmentToolbar.setTitleTextColor(Color.WHITE)
        paginatedFragmentToolbar.textAlignment=
            CoordinatorLayout.TEXT_ALIGNMENT_CENTER
        paginatedFragmentToolbar.setTitleTextAppearance(requireContext(),R.style.TextAppearance_Asobi_Headline4)
    }

    private fun setUpRecyclerViewAdapter():PagingDataAdapter<ViewComics,RecyclerView.ViewHolder> = composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
        viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
            itemView.setOnClickListener { onComicClicked(item) }
        }
    }, bindViewHolder = { viewHolder: RecyclerView.ViewHolder, item: ViewComics?, _ ->
        (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
    })

    private fun setUpSwipeRefreshWidgetState(isRefreshing:Boolean){
        paginatedFragmentSwipeRefreshLayout.isRefreshing = isRefreshing
    }
    private fun setUpSwipeRefreshWidget(){
        with(paginatedFragmentSwipeRefreshLayout){
            doOnNextLayout {
                // similar to Modifier.maxWidth() in compose
                setContentToMaxWidth(paginatedFragmentSwipeRefreshLayout)
            }
            setOnRefreshListener { pagingDataAdapter?.refresh() }
        }
    }
    private fun setUpMainFragmentRecyclerView(){
        val screenWidth= resourcesInstance().displayMetrics.run {
            widthPixels/density }
        with(paginatedFragmentRecyclerView){
            doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(bottom= viewPaddingState.bottom + systemInsets.bottom)
            }
            setHasFixedSize(true)
            scrollToTop()
            adapter = pagingDataAdapter
            /*By default the medium density is 160f so we minus 4 just increase to accommodate smaller screens and come up with a proper
            * no of span count for our grid layout */
            layoutManager = gridLayoutManager(spanCount = (screenWidth/156f).toInt())
        }
    }
    private fun animateAppBarColorsOnScroll(){
        paginatedFragmentRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.canScrollVertically(-1).not()){
                    changeStatusBarToTransparentInFragment(resources.getColor(R.color.transparent,null))
                    applyBarElevationAndBackgroundColor(resources.getColor(R.color.matte,null),0f)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy>0){
                    // scrolling down
                    doActionIfWeAreOnDebug { logger.i("dy>0 scrolling down $dy") }
                    changeStatusBarToTransparentInFragment(getFragmentColor())
                    applyBarElevationAndBackgroundColor(getFragmentColor(), 4f)
                }else if (dy<-1) {
                    // scrolling up
                    doActionIfWeAreOnDebug {  logger.i("dy<-1 scrolling up $dy") }
                    changeStatusBarToTransparentInFragment(getFragmentColor())
                    applyBarElevationAndBackgroundColor(getFragmentColor(),4f)
                }
            }
        })
    }
    /* End: Setting up Ui Components */
    override fun onDestroy() {
        super.onDestroy()
        pagingDataAdapter =null
    }

    companion object{
        private const val isFragmentHiddenTag ="paginatedFragmentIsHiddenTag"
    }
}
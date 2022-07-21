package com.gibsonruitiari.asobi.ui.comicfilter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicFilterFragmentBinding
import com.gibsonruitiari.asobi.databinding.SelectableFilterChipItemBinding
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.FilterChip
import com.gibsonruitiari.asobi.utilities.extensions.doOnApplyWindowInsets
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.extensions.slideOffsetToAlpha
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
abstract class ComicsFilterBottomSheet:Fragment() {
    companion object{
        /* Threshold for when the contents of the bottom sheet should become invisible */
        private const val ALPHA_CONTENT_START =0.1f
        /* Threshold for when the contents of the bottom sheet should become visible */
        private const val ALPHA_CONTENT_END=0.3F
    }

    protected abstract fun resolveViewModelDelegate():ComicFilterViewModel
    private var selectedFilterChip:FilterChip?=null
    private lateinit var filterViewModel:ComicFilterViewModel
    private lateinit var comicFilterFragmentBinding:ComicFilterFragmentBinding
    private lateinit var behavior:BottomSheetBehavior<*>
    private var filterRecyclerViewAdapter: ListAdapter<FilterChip, BindingViewHolder<SelectableFilterChipItemBinding>> ?=null


    private val backPressedCallback = object :OnBackPressedCallback(false){
        override fun handleOnBackPressed() {
            if (::behavior.isInitialized && behavior.state==STATE_EXPANDED){
                behavior.state = STATE_HIDDEN
            }
        }
    }
    /*Bottom sheet states' values range from 1-5, by default we shouldn't have a pending sheet state so we set it to
    * -1  */
    private var pendingSheetState = -1
    private var BindingViewHolder<SelectableFilterChipItemBinding>.item by viewHolderDelegate<FilterChip>()
    private fun BindingViewHolder<SelectableFilterChipItemBinding>.bindFilterChip(filterChip: FilterChip){
        this.item = filterChip
        binding.filterLabel.text= filterChip.text
        binding.filterLabel.isChecked = filterChip.isSelected
        val tintColor=if (filterChip.color != Color.TRANSPARENT) filterChip.color else ContextCompat.getColor(binding.filterLabel.context,R.color.default_tag_color)
        binding.filterLabel.chipIconTint = ColorStateList.valueOf(tintColor)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        comicFilterFragmentBinding = ComicFilterFragmentBinding.inflate(inflater,
        container,false)
        /* Use system inset padding values to ensure recycler-view's contents are above the
        * navigation bar  */
        comicFilterFragmentBinding.recyclerviewGenreFilters.doOnApplyWindowInsets{
            view,insets, paddingValues->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.ime())
            view.updatePadding(bottom=paddingValues.bottom+systemInsets.bottom)
        }
        return comicFilterFragmentBinding.root
    }
    @Deprecated("")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        behavior = from(comicFilterFragmentBinding.filterSheet)
        val peekHeight = behavior.peekHeight
        val marginBottom = comicFilterFragmentBinding.root.marginBottom
        applyWindowInsetsToRootComicFilterView(peekHeight, marginBottom)
        attachBottomSheetBehaviorWithACallback()
        setUpComicFilterSheetUiComponents()
        updateBehaviorStateDependingOnPendingSheetState()
        filterViewModel = resolveViewModelDelegate()
        filterRecyclerViewAdapter = createFilterRecyclerViewAdapterInstance()
        setUpComicFilterSheetRecyclerViewWidget()

        updateBackPressedCallbackEnabled(behavior.state)
        resetFilterChoice()
    }
    private fun updateBehaviorStateDependingOnPendingSheetState(){
        if (pendingSheetState!=-1){
            behavior.state = pendingSheetState
            pendingSheetState=-1 // reset to default
        }
    }
    private fun attachBottomSheetBehaviorWithACallback(){
        behavior.addBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /* make them[contents] visible/invisible depending on whether the offset range 0-1 */
                updateBottomSheetFilterContentsAlpha(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                updateBackPressedCallbackEnabled(newState)
            }
        })
    }
    private fun applyWindowInsetsToRootComicFilterView(peekHeight:Int, marginBottom:Int){
        /* Apply gesture insets so that the container scrolls within the system ui  */
        comicFilterFragmentBinding.root.doOnApplyWindowInsets { v, windowInsetsCompat, _ ->
            val gestureInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type
                .systemGestures())
            behavior.peekHeight = gestureInsets.bottom + peekHeight
            // Update the peek height so that it is above the navigation bar
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = marginBottom + gestureInsets.top
            }
        }
    }
    private fun setUpComicFilterSheetUiComponents(){
        comicFilterFragmentBinding.collapseArrow.setOnClickListener {
            behavior.state = if (behavior.skipCollapsed) STATE_HIDDEN else STATE_COLLAPSED
        }
        comicFilterFragmentBinding.filterSheet.doOnLayout {
            val slideOffset = when(behavior.state){
                STATE_EXPANDED->1f
                STATE_COLLAPSED->0f
                else-> -1f
            }
            updateBottomSheetFilterContentsAlpha(slideOffset)
        }
    }
    private fun setUpComicFilterSheetRecyclerViewWidget(){
        comicFilterFragmentBinding.recyclerviewGenreFilters.apply {
            setHasFixedSize(true)
            adapter = filterRecyclerViewAdapter!!
            addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    comicFilterFragmentBinding.filtersHeaderShadow.isActivated = recyclerView.canScrollVertically(-1)
                }
            })
            addItemDecoration(FlexboxItemDecoration(context).apply {
                setDrawable(context.getDrawable(R.drawable.divide_empty_margin))
                setOrientation(FlexboxItemDecoration.VERTICAL)
            })
        }
    }
    private fun createFilterRecyclerViewAdapterInstance():ListAdapter<FilterChip,BindingViewHolder<SelectableFilterChipItemBinding>> = listAdapterOf(initialItems = filterViewModel.genresList.value,
        viewHolderCreator = {parent: ViewGroup, _: Int ->
            parent.viewHolderFrom(SelectableFilterChipItemBinding::inflate).apply {
                itemView.setOnClickListener {
                    selectedFilterChip=selectedFilterChip?.copy(isSelected = false)

                    selectedFilterChip = item
                    filterViewModel.setGenre(selectedFilterChip?.genres!!)
                    hideFiltersSheet()
                }
            }
        }, viewHolderBinder = {holder: BindingViewHolder<SelectableFilterChipItemBinding>, item: FilterChip, _: Int -> holder.bindFilterChip(item)})
    /* this method is called after the fragment is attached to view hierarchy of the parent
    * Thus initializing the behavior to be associated with our sheet whilst our root component/view isn't
    * a direct child of Coordinator Layout throws an massive InflateException Error
    * thus the reason why we do inflation and initialization of most components in onActivityCreated despite
    * it being deprecated*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            launch { filterViewModel.genresList.collect {
                filterRecyclerViewAdapter?.submitList(it) }}
            launch {
                filterViewModel.contentAlpha.collectLatest {
                   setAlphaStateForFilterSheetUiComponents(it)
                   setUpUiComponentsClickAbilityDependingContentAlpha(it)
                }
            }
            launch { filterViewModel.selectedFilterChip.collectLatest {
                selectedFilterChip=it
            } }

        }
        initializeBasicUiComponents()
    }
    private fun setUpUiComponentsClickAbilityDependingContentAlpha(alpha: Float){
        comicFilterFragmentBinding.collapseArrow.isClickable = alpha>0f
        comicFilterFragmentBinding.resetFilterBtn.isClickable = alpha>0f
    }
    private fun setAlphaStateForFilterSheetUiComponents(alpha:Float){
        comicFilterFragmentBinding.collapseArrow.alpha= alpha
        comicFilterFragmentBinding.resetFilterBtn.alpha=alpha
        comicFilterFragmentBinding.recyclerviewGenreFilters.alpha=alpha
        comicFilterFragmentBinding.filterSheetHeaderText.alpha=alpha
        comicFilterFragmentBinding.recyclerviewGenreFilters.alpha=alpha
    }
    private fun initializeBasicUiComponents(){
        comicFilterFragmentBinding.filterSheetHeaderText.text= getString(R.string.filter_comics_by_genre)
    }
    private fun resetFilterChoice(){
        comicFilterFragmentBinding.resetFilterBtn.setOnClickListener {
            filterViewModel.resetFilterChoice()
        }
    }
    private fun updateBottomSheetFilterContentsAlpha(slideOffset:Float){
        filterViewModel.setContentAlpha(slideOffsetToAlpha(slideOffset, ALPHA_CONTENT_START, ALPHA_CONTENT_END))
    }
    private fun updateBackPressedCallbackEnabled(state:Int){
        backPressedCallback.isEnabled = !(state == STATE_COLLAPSED || state == STATE_HIDDEN)
    }
    /* To be used by GenreFragment when the filter-button is clicked */
    fun showFiltersSheet(){
        if (::behavior.isInitialized){
            behavior.state = STATE_EXPANDED
        }else pendingSheetState = STATE_EXPANDED
    }
    private fun hideFiltersSheet(){
        if (::behavior.isInitialized){
            behavior.state = STATE_HIDDEN
        }else pendingSheetState= STATE_HIDDEN
    }

}
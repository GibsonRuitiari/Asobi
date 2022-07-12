package com.gibsonruitiari.asobi.ui.comicfilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicFilterFragmentBinding
import com.gibsonruitiari.asobi.utilities.extensions.doOnApplyWindowInsets
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.extensions.slideOffsetToAlpha
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ComicsFilterBottomSheet:Fragment() {
    companion object{
        /* Threshold for when the contents of the bottom sheet should become invisible */
        private const val ALPHA_CONTENT_START =0.1f
        /* Threshold for when the contents of the bottom sheet should become visible */
        private const val ALPHA_CONTENT_END=0.3F
    }
    private val filterViewModel:ComicFilterViewModel by inject()
    private lateinit var binding:ComicFilterFragmentBinding
    private lateinit var behavior:BottomSheetBehavior<*>
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ComicFilterFragmentBinding.inflate(inflater,
        container,false).apply {
            collapseArrow.alpha = filterViewModel.contentAlpha.value
        }
        /* Use system inset padding values to ensure recycler-view's contents are above the
        * navigation bar  */
        binding.recyclerviewGenreFilters.doOnApplyWindowInsets{
            view,insets, paddingValues->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.ime())
            view.updatePadding(bottom=paddingValues.bottom+systemInsets.bottom)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(binding.filterSheet)
        binding.recyclerviewGenreFilters.apply {
            //adapter =//
            setHasFixedSize(true)
            itemAnimator=null
            addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    binding.filtersHeaderShadow.isActivated = recyclerView.canScrollVertically(-1)
                }
            })
            addItemDecoration(FlexboxItemDecoration(context).apply {
                setDrawable(context.getDrawable(R.drawable.divide_empty_margin))
                setOrientation(FlexboxItemDecoration.VERTICAL)
            })
        }
        val peekHeight = behavior.peekHeight
        val marginBottom = binding.root.marginBottom
        /* Apply gesture insets so that the container scrolls within the system ui  */
        binding.root.doOnApplyWindowInsets { v, windowInsetsCompat, viewPaddingState ->
            val gestureInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type
                .systemGestures())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = marginBottom + gestureInsets.top
            }
        }
        behavior.addBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /* make them[contents] visible/invisible depending on whether the offset range 0-1 */
                updateBottomSheetFilterContentsAlpha(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
               updateBackPressedCallbackEnabled(newState)
            }
        })
        binding.collapseArrow.setOnClickListener {
            behavior.state = if (behavior.skipCollapsed) STATE_HIDDEN else STATE_COLLAPSED
        }
        binding.filterSheet.doOnLayout {
            val slideOffset = when(behavior.state){
                STATE_EXPANDED->1f
                STATE_COLLAPSED->0f
                else-> -1f
            }
            updateBottomSheetFilterContentsAlpha(slideOffset)
        }
        if (pendingSheetState!=-1){
            behavior.state = pendingSheetState
            pendingSheetState=-1 // reset to default
        }
        updateBackPressedCallbackEnabled(behavior.state)
        resetFilterChoice()
        launchAndRepeatWithViewLifecycle {
            launch {
                filterViewModel.contentAlpha.collectLatest {
                    binding.collapseArrow.alpha= it
                    binding.resetFilterBtn.alpha=it
                    binding.filterSheetHeaderText.alpha=it
                    binding.recyclerviewGenreFilters.alpha=it

                    binding.collapseArrow.isClickable = it>0f
                    binding.resetFilterBtn.isClickable = it>0f

                }
            }
            launch { filterViewModel.genresList.collectLatest {
             binding.recyclerviewGenreFilters
            }}
        }
        initializeBasicUiComponents()
    }
    private fun initializeBasicUiComponents(){
        binding.filterSheetHeaderText.text= getString(R.string.filter_comics_by_genre)
    }
    private fun resetFilterChoice(){
        binding.resetFilterBtn.setOnClickListener {
            filterViewModel.resetFilterChoice()
        }
    }
    private fun updateBottomSheetFilterContentsAlpha(slideOffset:Float){
        filterViewModel.setContentAlpha(slideOffsetToAlpha(slideOffset, ALPHA_CONTENT_START, ALPHA_CONTENT_END))
    }
    private fun updateBackPressedCallbackEnabled(state:Int){
        backPressedCallback.isEnabled = !(state == STATE_COLLAPSED || state == STATE_HIDDEN)
    }
    fun showFiltersSheet(){
        if (::behavior.isInitialized){
            behavior.state = STATE_EXPANDED
        }else pendingSheetState = STATE_EXPANDED
    }

}
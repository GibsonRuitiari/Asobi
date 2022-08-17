package com.gibsonruitiari.asobi.ui.comicssearch

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import org.koin.android.ext.android.inject

@VisibleForTesting
class ComicsSearchResultsScreen: Fragment() {
    /* Start: initialization of view variables */
    private lateinit var searchResultsScreenToolbar:Toolbar
    private lateinit var searchResultsScreenSearchTitle:AppCompatTextView
    private lateinit var searchResultsScreenSearchSubtitle:AppCompatTextView
    private lateinit var searchResultsScreenRecyclerView: RecyclerView
    private lateinit var searchResultsTextInputLayout: TextInputLayout
    private lateinit var searchResultsEditText:EditText
    /* End: initialization of view variables */

    private var toolbarExpanded:Boolean =false
    private val logger:Logger by inject()

    /* Start: Fragment view */
    internal class ComicsSearchResultsView (context:Context)
        :ParentFragmentsView(context){
        lateinit var searchExplanationSubtitle:AppCompatTextView
        lateinit var searchExplanationTitle:AppCompatTextView
        lateinit var searchResultsToolbar:Toolbar
        lateinit var searchResultsRecyclerView: RecyclerView
        lateinit var searchResultsTextInputLayout: TextInputLayout
        lateinit var searchResultsEditText:EditText
        init {
            searchScreenResultsAppBar(context)
            searchScreenResultsConstraintLayout(context)
        }
        private fun searchScreenResultsAppBar(context: Context){
            val appBarLayout  = AppBarLayout(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                elevation= noElevationValue

            }
            addView(appBarLayout)
            searchResultsToolbar = searchScreenResultsMaterialToolbar(context)
            appBarLayout.addView(searchResultsToolbar)
        }
        private fun searchScreenResultsMaterialToolbar(context: Context):Toolbar{
            val materialToolbar = Toolbar(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                background = context.getDrawable(R.drawable.toolbar_bg)
                setTitleTextColor(Color.WHITE)
                textAlignment= TEXT_ALIGNMENT_CENTER
                title = context.getString(R.string.search_label)
                /* elevation not being changed not working for some reason? */
                elevation= noElevationValue
                (layoutParams as AppBarLayout.LayoutParams).setMargins(toolbarStartMargin.dp)
            }
            searchResultsTextInputLayout= searchScreenTextInputLayout(materialToolbar.context)
            materialToolbar.addView(searchResultsTextInputLayout)
            return materialToolbar
        }
        private fun searchScreenTextInputLayout(context: Context):TextInputLayout{
            val textInputLayout = TextInputLayout(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
                isHintEnabled=false
                visibility=View.GONE
                boxBackgroundMode=TextInputLayout.BOX_BACKGROUND_NONE
                setEndIconTintList(defaultColorStateList)
                endIconMode=END_ICON_CLEAR_TEXT
            }
             searchResultsEditText = EditText(textInputLayout.context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
                inputType=InputType.TYPE_CLASS_TEXT
                 maxLines=1
                 setTextColor(ColorStateList.valueOf(Color.WHITE))
                 background = context.getDrawable(R.color.davy_grey)
                 gravity =Gravity.CENTER_VERTICAL
                 hint = context.resources.getString(R.string.search_comics_hint)
                imeOptions= EditorInfo.IME_ACTION_SEARCH
            }
            textInputLayout.addView(searchResultsEditText)
            return textInputLayout
        }
        private fun searchScreenResultsConstraintLayout(context: Context){
            val constraintLayout = ConstraintLayout(context).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
                id=ViewCompat.generateViewId()
                fitsSystemWindows=true
            }
            addView(constraintLayout)
            val constraintSet = ConstraintSet()
            searchResultsRecyclerView = searchResultsScreenRecyclerView(context, constraintSet)
            constraintLayout.addView(searchResultsRecyclerView)
            searchExplanationTitle = searchScreenResultsSearchTitle(context,constraintSet)
            constraintLayout.addView(searchExplanationTitle)
            searchExplanationSubtitle = searchScreenResultsSearchSubtitle(context,constraintSet,searchExplanationTitle.id)
            constraintLayout.addView(searchExplanationSubtitle)
            // add views
            constraintSet.applyTo(constraintLayout)
        }
        private fun searchResultsScreenRecyclerView(context: Context,constraintSet: ConstraintSet):RecyclerView{
            val recyclerView = RecyclerView(context).apply { id=ViewCompat.generateViewId(); visibility=View.GONE
                val animation= AnimationUtils.loadLayoutAnimation(this.context, R.anim.layout_animation_scale_in)
                animate(animation)}
            val recyclerViewId= recyclerView.id
            constraintSet.setViewLayoutParams(recyclerViewId, ConstraintSet.MATCH_CONSTRAINT,ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.applyMargin(recyclerViewId, marginTop = toolbarStartMargin)
            constraintSet constrainTopToParent recyclerViewId
            constraintSet constrainEndToParent recyclerViewId
            constraintSet constrainStartToParent recyclerViewId
            constraintSet constrainBottomToParent  recyclerViewId
            return recyclerView
        }
        private fun searchScreenResultsSearchTitle(context: Context,constraintSet: ConstraintSet):AppCompatTextView{
            val searchTitle=context.getString(R.string.search_explanation)
            val appCompatTextView = AppCompatTextView(context).apply {
                id=ViewCompat.generateViewId()
                text=searchTitle
                setTextColor(Color.WHITE)
                setTextAppearance(R.style.TextAppearance_Asobi_Headline4)
            }
            val appCompatTextViewId= appCompatTextView.id
            constraintSet.setViewLayoutParams(appCompatTextViewId, ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)
            constraintSet constrainTopToParent appCompatTextViewId
            constraintSet constrainEndToParent  appCompatTextViewId
            constraintSet constrainStartToParent appCompatTextViewId
            constraintSet constrainBottomToParent appCompatTextViewId

            return appCompatTextView
        }
        private fun searchScreenResultsSearchSubtitle(context: Context,
                                                      constraintSet: ConstraintSet, searchTitleTextId:Int):AppCompatTextView{
            val searchResultsScreenSubtitle = context.getString(R.string.search_explanation_subtitle)
            val appCompatTextView = AppCompatTextView(context).apply {
                id=ViewCompat.generateViewId()
                text =searchResultsScreenSubtitle
                setTextColor(Color.WHITE)
                setTextAppearance(R.style.TextAppearance_Asobi_Subtitle1)
            }
            val searchSubtitleId=appCompatTextView.id
            constraintSet.setViewLayoutParams(searchSubtitleId,ConstraintSet.WRAP_CONTENT, ConstraintSet.WRAP_CONTENT)
            constraintSet.applyMargin(searchSubtitleId, marginTop = toolbarStartMargin.dp)
            constraintSet constrainStartToParent searchSubtitleId
            constraintSet constrainEndToParent searchSubtitleId
            constraintSet.connect(searchSubtitleId,ConstraintSet.TOP, searchTitleTextId,ConstraintSet.BOTTOM)
            return appCompatTextView
        }
    }
    /* End: Fragment view */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, true){
           if (toolbarExpanded) animateToolbarChanges()
           else {
               cleanUpSearchQuery()
               isEnabled=false
               activity?.onBackPressed()
           }
       }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView = ComicsSearchResultsView(requireContext())
        searchResultsScreenToolbar = fragmentView.searchResultsToolbar
        searchResultsScreenSearchTitle = fragmentView.searchExplanationTitle
        searchResultsScreenSearchSubtitle = fragmentView.searchExplanationSubtitle
        searchResultsScreenRecyclerView = fragmentView.searchResultsRecyclerView
        searchResultsEditText = fragmentView.searchResultsEditText
        searchResultsTextInputLayout = fragmentView.searchResultsTextInputLayout
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeToolbarLayoutMarginOnClick()
        setUpSearchResultsScreenRecyclerView()

        setUpTextInputLayoutActionListener()

    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
       if (hidden && toolbarExpanded) {
           doActionIfWeAreOnDebug { logger.i("hidden and toolbar expanded") }
           animateToolbarChanges()
       }
    }
    private fun setUpTextInputLayoutActionListener(){
        searchResultsTextInputLayout.editText?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                val query = searchResultsTextInputLayout.editText?.text.toString()
                doActionIfWeAreOnDebug { logger.i("search query $query") }
                dismissKeyboard(v)
                return@setOnEditorActionListener  true
            }
            return@setOnEditorActionListener false
        }
        if(searchResultsTextInputLayout.editText?.text.isNullOrBlank()){
            searchResultsTextInputLayout.editText?.let { if (it.hasFocus()) showKeyboard(it) }
        }
    }
    private fun changeToolbarLayoutMarginOnClick(){
        searchResultsScreenToolbar.setOnClickListener { animateToolbarChanges() }
    }
    private fun setUpSearchResultsScreenRecyclerView(){
        val screenWidth= resourcesInstance().displayMetrics.run {
            widthPixels/density }
        with(searchResultsScreenRecyclerView){
            doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(bottom= viewPaddingState.bottom + systemInsets.bottom)
            }
            setHasFixedSize(true)
            scrollToTop()
           adapter = searchScreenResultsRecyclerViewAdapter
            /*By default the medium density is 160f so we minus 4 just increase to accommodate smaller screens and come up with a proper
            * no of span count for our grid layout */
            layoutManager = gridLayoutManager(spanCount = (screenWidth/156f).toInt())
        }
    }
    /* Start: Fragment's specific utility methods */
    private fun animateToolbarChanges(animationInterpolator:TimeInterpolator=AccelerateDecelerateInterpolator(),
    animationDuration:Long=250L){
        val expandedDrawable = shapeDrawable.apply { paint.color=resources.getColor(R.color.davy_grey,null)}
        val marginLayoutParams = searchResultsScreenToolbar.layoutParams as ViewGroup.MarginLayoutParams
        toolbarExpanded=if (toolbarExpanded.not()){
            val animator = ValueAnimator.ofInt(toolbarStartMargin, toolbarEndMargin).apply { interpolator=animationInterpolator;duration=animationDuration }
            animator.addUpdateListener {
                val recentlyAnimatedValue = it.animatedValue as Int
                marginLayoutParams.setMargins(recentlyAnimatedValue.dp)
                searchResultsScreenToolbar.layoutParams=marginLayoutParams
            }
            animator.doOnEnd { searchResultsScreenToolbar.background = expandedDrawable;searchResultsScreenToolbar.title="" }
            animator.start()
            fadeSearchResultsTextInputLayout(1f){
                searchResultsTextInputLayout.visibility=View.VISIBLE
             searchResultsTextInputLayout.editText?.let { it.requestFocus();showKeyboard(it.findFocus()) }}
            true
        }else{
            val animator=ValueAnimator.ofInt(toolbarEndMargin, toolbarStartMargin).apply { interpolator=animationInterpolator;duration=animationDuration}
            animator.addUpdateListener {
                val recentlyAnimatedValue = it.animatedValue as Int
                marginLayoutParams.setMargins(recentlyAnimatedValue.dp)
                searchResultsScreenToolbar.layoutParams = marginLayoutParams
            }
            animator.doOnEnd { searchResultsScreenToolbar.background=resources.getDrawable(R.drawable.toolbar_bg,null);searchResultsScreenToolbar.title=resources.getString(R.string.search_comics_hint)}
            animator.start()
        fadeSearchResultsTextInputLayout(0f){searchResultsTextInputLayout.visibility=View.GONE; cleanUpSearchQuery()}
            false
        }
    }
    private fun cleanUpSearchQuery(){
        searchResultsTextInputLayout.editText?.clearFocus()
        searchResultsTextInputLayout.editText?.setText("")
    }
    private inline fun fadeSearchResultsTextInputLayout(alpha:Float,animationInterpolator: TimeInterpolator=AccelerateDecelerateInterpolator(),
    crossinline endAction:()->Unit){
        searchResultsTextInputLayout.animate().alpha(alpha)
            .setInterpolator(animationInterpolator)
            .setDuration(500)
            .withEndAction { endAction.invoke() }
            .start()
    }
    private fun showKeyboard(view: View) {
        WindowInsetsControllerCompat(requireActivity().window,view).show(WindowInsetsCompat.Type.ime())
    }

    private fun dismissKeyboard(view: View) {
        WindowInsetsControllerCompat(requireActivity().window,view).hide(WindowInsetsCompat.Type.ime())
    }
    /* End: Fragment's specific utility methods */
    private val searchScreenResultsRecyclerViewAdapter = listAdapterOf(initialItems = emptyList<ViewComics>(), viewHolderCreator = {parent, viewType ->
        parent.viewHolderFrom(ComicItemLayoutBinding::inflate)
    }, viewHolderBinder ={holder, item, _ ->
        holder.bind(item) })
    private var BindingViewHolder<ComicItemLayoutBinding>.searchResultsComics by viewHolderDelegate<ViewComics>()
    private fun BindingViewHolder<ComicItemLayoutBinding>.bind(viewComics: ViewComics){
        this.searchResultsComics = viewComics
        with(binding){
            comicsImageView.loadPhotoUrl(viewComics.comicThumbnail)
        }
    }
    companion object{
        private const val toolbarStartMargin=16
        private const val toolbarEndMargin=0
        private const val noElevationValue=0f
        private val colorStates = intArrayOf(Color.GRAY,Color.WHITE) // pressed, -pressed
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(-android.R.attr.state_pressed))
        val defaultColorStateList = ColorStateList(states, colorStates)
        val shapeDrawable = ShapeDrawable(RectShape()).apply {
            this.setPadding(0,0,0,0)}

    }
}
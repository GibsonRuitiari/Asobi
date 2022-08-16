package com.gibsonruitiari.asobi.ui.comicssearch


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.databinding.GenreComicItemBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import com.gibsonruitiari.asobi.ui.uiModels.UiGenreModel
import com.gibsonruitiari.asobi.utilities.extensions.*
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment that displays a list of available genres
 */
class ComicsGenreScreen:Fragment() {
    /* Start: ComicsGenreScreen fragment variables declaration */
    private val comicsSearchViewModel: ComicsSearchViewModel by viewModel()
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel()
    private val mainActivityViewModel:MainActivityViewModel by sharedViewModel(owner = {requireParentFragment()})
    private val logger:Logger by inject()
    private var loadingJob:Job?=null

    private lateinit var comicsGenreScreenRootLayout:CoordinatorLayout
    private lateinit var comicsGenreRecyclerView:RecyclerView
    private lateinit var comicsGenreScreenSearchButton: MaterialButton
    private lateinit var comicsGenreSearchTextLabel:AppCompatTextView


    /* End: ComicsGenreScreen fragment variables declaration */

    /* Start: ComicsGenreScreen fragment view */
    internal class ComicsGenreScreenView(context: Context):ParentFragmentsView(context){
        lateinit var searchButton:MaterialButton
        lateinit var searchTextLabel:AppCompatTextView
        lateinit var genresRecyclerView:RecyclerView
        init {
            constraintLayoutContainer(context)
        }
        private fun constraintLayoutContainer(context: Context){
            val constraintLayout=ConstraintLayout(context).apply {
                id=ViewCompat.generateViewId()
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
                fitsSystemWindows=true
            }
            addView(constraintLayout)
            val constraintSet = ConstraintSet()
             searchTextLabel=searchTextLabel(context,constraintSet)
            constraintLayout.addView(searchTextLabel)
             searchButton = comicsGenreScreenSearchButton(context,constraintSet,searchTextLabel.id)
            constraintLayout.addView(searchButton)
            val genreLabel = comicsGenreLabel(context,constraintSet,searchButton.id)
            constraintLayout.addView(genreLabel)
             genresRecyclerView=comicsGenreScreenRecyclerView(context,constraintSet,genreLabel.id)
            constraintLayout.addView(genresRecyclerView)
            constraintSet.applyTo(constraintLayout)
        }
        private fun searchTextLabel(context: Context,
        constraintSet: ConstraintSet):AppCompatTextView{
            val appcompatTextView=AppCompatTextView(context).apply {
                id=ViewCompat.generateViewId()
                setTextColor(Color.WHITE)
                text=context.resources.getString(R.string.search_label)
                setTextAppearance(R.style.TextAppearance_Asobi_Headline5)
            }
            constraintSet.setViewLayoutParams(appcompatTextView.id,
            ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT)
            constraintSet constrainTopToParent appcompatTextView.id
            constraintSet constrainStartToParent appcompatTextView.id
            constraintSet.applyMargin(appcompatTextView.id,24.dp,24.dp,24.dp,24.dp)
            return appcompatTextView
        }
        private fun comicsGenreScreenSearchButton(context: Context,
                                                  constraintSet: ConstraintSet,searchLabelId:Int):MaterialButton{
            val searchButton=MaterialButton(context).apply {
                id=ViewCompat.generateViewId()
                iconGravity = 0x1
                textAlignment = 0x2
                setTextColor(context.resources.getColor(R.color.davy_grey,null))
                textSize = 18f // will be automatically converted to 18sp
                contentDescription=context.resources.getString(R.string.search_comics_hint)
                iconTint= defaultColorStateList
                text=context.resources.getString(R.string.search_comics_hint)
                isAllCaps=false
                iconSize=30.dp
                setTextColor(defaultColorStateList)
                setBackgroundColor(Color.WHITE)
                icon=context.resources.getDrawable(R.drawable.ic_baseline_search_24,null)
            }
            constraintSet.setViewLayoutParams(searchButton.id, width = ConstraintSet.MATCH_CONSTRAINT,height=65.dp)
            constraintSet.applyMargin(searchButton.id,marginTop=16.dp, marginStart = 16.dp, marginEnd = 16.dp)
            constraintSet constrainEndToParent searchButton.id
            constraintSet constrainStartToParent searchButton.id
            constraintSet.connect(searchButton.id,ConstraintSet.TOP,searchLabelId,ConstraintSet.BOTTOM)
            return searchButton
        }
        private fun comicsGenreLabel(context: Context,constraintSet: ConstraintSet,
        searchButtonId:Int):AppCompatTextView{
            val appCompatTextView=AppCompatTextView(context).apply {
                id=ViewCompat.generateViewId()
                setTextColor(Color.WHITE)
                setTextAppearance(R.style.TextAppearance_Asobi_Label)
                text =context.resources.getString(R.string.browse_all)
            }
            constraintSet.setViewLayoutParams(appCompatTextView.id,ConstraintSet.WRAP_CONTENT,
            ConstraintSet.WRAP_CONTENT)
            constraintSet constrainStartToParent appCompatTextView.id
            constraintSet.connect(appCompatTextView.id,ConstraintSet.TOP,searchButtonId,
            ConstraintSet.BOTTOM)
            constraintSet.applyMargin(appCompatTextView.id,16.dp,16.dp,16.dp,16.dp)
            return appCompatTextView
        }
        private fun comicsGenreScreenRecyclerView(context: Context,constraintSet: ConstraintSet,
        genreLabelId:Int):RecyclerView{
            val recyclerView=RecyclerView(context).apply { id=ViewCompat.generateViewId()}
            val recyclerViewId=recyclerView.id
            constraintSet.setViewLayoutParams(recyclerViewId,ConstraintSet.MATCH_CONSTRAINT,
            ConstraintSet.MATCH_CONSTRAINT)
            constraintSet constrainStartToParent recyclerViewId
            constraintSet constrainEndToParent recyclerViewId
            constraintSet constrainBottomToParent  recyclerViewId
            constraintSet.connect(recyclerViewId,ConstraintSet.TOP,genreLabelId,ConstraintSet.BOTTOM)
            constraintSet.applyMargin(recyclerViewId, marginTop =8.dp)
            return recyclerView
        }
    }
    /* Start: ComicsGenreScreen fragment view */



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        comicsGenreScreenRootLayout = ComicsGenreScreenView(requireContext())
        comicsGenreRecyclerView=(comicsGenreScreenRootLayout as ComicsGenreScreenView).genresRecyclerView
        comicsGenreScreenSearchButton=(comicsGenreScreenRootLayout as ComicsGenreScreenView).searchButton
        comicsGenreSearchTextLabel =(comicsGenreScreenRootLayout as ComicsGenreScreenView).searchTextLabel
        return comicsGenreScreenRootLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null) applyWindowInsetsToParent()
        setUpMainFragmentRecyclerView()
        loadData(isHidden)
        searchFragmentRecyclerViewOnScrollListener()
        comicsGenreScreenSearchButton.setOnSafeClickListener {
            doActionIfWeAreOnDebug { logger.i("search button clicked opening search results screen") }
            mainActivityViewModel.openComicsSearchResultsScreen()
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        loadData(hidden)
    }
    private fun loadData(isFragmentHidden:Boolean){
        if (isFragmentHidden){
            loadingJob?.cancel()
        }else{
            observeGenresDataFromViewModel()
        }
    }
    private fun observeGenresDataFromViewModel(){
        loadingJob?.cancel()
        loadingJob=launchAndRepeatWithViewLifecycle {
            comicsSearchViewModel.genres.collectLatest {
                genresAdapter.submitList(it)
            }
        }
    }
    private fun applyWindowInsetsToParent(){
        comicsGenreScreenRootLayout.postDelayed({comicsGenreScreenRootLayout.requestApplyInsetsWhenAttached()},500)

    }
    private fun searchFragmentRecyclerViewOnScrollListener(){
        with(comicsGenreRecyclerView){
            addOnScrollListener(object:RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy> 25){
                        comicsGenreSearchTextLabel.fade(0f, transitionDuration = 500, animationInterpolator = createPathInterpolator(easeOutInterpolatorArray)).start()
                        doActionIfWeAreOnDebug { logger.i("scrolling down") }
                    }else if (dy<-25){
                        comicsGenreSearchTextLabel.fade(1f, transitionDuration = 500, animationInterpolator =createPathInterpolator(easeOutInterpolatorArray)).start()
                        doActionIfWeAreOnDebug { logger.i("scrolling up") }
                    }
                }
            })
        }
    }


    private fun setUpMainFragmentRecyclerView(){
        val screenWidth= resourcesInstance().displayMetrics.run { widthPixels/density }
        with(comicsGenreRecyclerView){
            elevation =0f
            this.doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
                val systemInsets = windowInsetsCompat.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
                view.updatePadding(bottom= viewPaddingState.bottom + systemInsets.bottom+20.dp)
            }
            this.doOnNextLayout {
                setContentToMaxWidth(this)
            }
            this.setHasFixedSize(true)
            this.scrollToTop()
            this.adapter = genresAdapter
            val animation= AnimationUtils.loadLayoutAnimation(this.context, R.anim.grid_layout_animation_from_bottom)
            animate(animation)
            /*By default the medium density is 160f so we minus 4 just increase to accommodate smaller screens and come up with a proper
            * no of span count for our grid layout */
            this.layoutManager = this.gridLayoutManager(spanCount = (screenWidth/156f).toInt())

        }
}


    private var BindingViewHolder<GenreComicItemBinding>.genres by viewHolderDelegate<UiGenreModel>()
    private fun BindingViewHolder<GenreComicItemBinding>.bindComicGenres(comicGenres:UiGenreModel) {
        this.genres = comicGenres
        with(binding){
            genreName.text= comicGenres.genreName
            genreCard.setCardBackgroundColor(comicGenres.genreColor)
        }
    }
    private val genresAdapter = listAdapterOf(initialItems = emptyList(), viewHolderCreator = { parent: ViewGroup, _: Int ->
        parent.viewHolderFrom(GenreComicItemBinding::inflate).apply {
            itemView.setOnClickListener {
                doActionIfWeAreOnDebug {
                    logger.i(genres.genreName)
                    comicsGenreScreenRootLayout.showSnackBar("${genres.genreName} clicked") }
                filterUiGenreModelToGenre(genres)?.let { genres -> comicsByGenreViewModel.setGenre(genres)
                mainActivityViewModel.openComicsByGenreScreenFromSearchScreen()
                }
            }
        }
    }, viewHolderBinder = {holder: BindingViewHolder<GenreComicItemBinding>, item: UiGenreModel, _: Int ->
        holder.bindComicGenres(item)
    })

    companion object{
        private val easeOutInterpolatorArray= floatArrayOf(0f,0f, 0.58f,1f)
        // states need to be a 2d array
        private val colorStates = intArrayOf(Color.GRAY,Color.BLACK) // pressed, -pressed
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(-android.R.attr.state_pressed))
        val defaultColorStateList = ColorStateList(states, colorStates)
        fun filterUiGenreModelToGenre(uiGenreModel: UiGenreModel):Genres? = Genres.values().firstOrNull { val originalName=uiGenreModel.genreName.replace(it.emoji ?: "","");it.genreName.contentEquals(originalName) }

    }

}
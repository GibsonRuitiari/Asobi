package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.animation.AnimatorInflater
import android.animation.LayoutTransition
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.TypefaceCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.vectordrawable.graphics.drawable.AnimationUtilsCompat
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ComicItemLayoutBinding
import com.gibsonruitiari.asobi.databinding.ComicsByGenreFragmentBinding
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.MainNavigationFragment
import com.gibsonruitiari.asobi.ui.comicfilter.ComicFilterFragment
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.composedPagedAdapter
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.uiModels.ViewComics
import com.gibsonruitiari.asobi.utilities.ExtendedFabBehavior
import com.gibsonruitiari.asobi.utilities.StatusBarScrimBehavior
import com.gibsonruitiari.asobi.utilities.extensions.gridLayoutManager
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.extensions.loadPhotoUrl
import com.gibsonruitiari.asobi.utilities.widgets.LoadingLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("UNCHECKED_CAST")
class ComicsByGenreFragment: MainNavigationFragment() {
    private var _comicsByGenreBinding:ComicsByGenreFragmentBinding?=null
    private val comicsByGenreBinding:ComicsByGenreFragmentBinding get() = _comicsByGenreBinding!!
    private val mainActivityViewModel:MainActivityViewModel by viewModel()

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
        val resourcesInstance = requireActivity().resources
        val colorSchemes=resourcesInstance.getIntArray(R.array.swipe_refresh_colors)
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
        val extendedFloatingActionButton = ExtendedFloatingActionButton(parentContainer.context).apply {
            id = ViewCompat.generateViewId()
            text = resourcesInstance.getText(R.string.filter)
            icon = resourcesInstance.getDrawable(R.drawable.ic_baseline_filter_list_24, null)
            contentDescription=resourcesInstance.getString(R.string.filter_comics_by_genre)
            layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            (layoutParams as CoordinatorLayout.LayoutParams).setMargins(resourcesInstance.getDimension(R.dimen.keyline_7).toInt(),
            resourcesInstance.getDimension(R.dimen.keyline_7).toInt(),
            resourcesInstance.getDimension(R.dimen.keyline_7).toInt(),
            resourcesInstance.getDimension(R.dimen.keyline_7).toInt())
            (layoutParams as CoordinatorLayout.LayoutParams).gravity =Gravity.BOTTOM+Gravity.END
            (layoutParams as CoordinatorLayout.LayoutParams).behavior= ExtendedFabBehavior(parentContainer.context)
        }
        parentContainer.addView(extendedFloatingActionButton)

        /* Add  constraint layout */
        val constraintLayout = ConstraintLayout(parentContainer.context).apply {
            id = ViewCompat.generateViewId()
            layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
        }
        parentContainer.addView(constraintLayout)

        /* Add swipe refresh layout */
        val swipeRefreshLayout = SwipeRefreshLayout(constraintLayout.context).apply {
            id = ViewCompat.generateViewId()
          //  (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
            setColorSchemeColors(*colorSchemes)
        }
        constraintLayout.addView(swipeRefreshLayout)
        val set= ConstraintSet()
        set.clone(constraintLayout)
        /* set constraints for swipe refresh layout*/
        set.constrainWidth(swipeRefreshLayout.id,0)
        set.constrainHeight(swipeRefreshLayout.id,0) // spread as far as possible
        set.connect(swipeRefreshLayout.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(swipeRefreshLayout.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(swipeRefreshLayout.id,ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(swipeRefreshLayout.id,ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.applyTo(constraintLayout)

        /* add frame layout for swipe refresh layout*/
        val frameLayoutContainer = FrameLayout(swipeRefreshLayout.context).apply {
            id= ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
            /* animate layout changes using the default LayoutTransition() */
            layoutTransition= LayoutTransition()
        }
        swipeRefreshLayout.addView(frameLayoutContainer)

        /* Stack things up on the frame layout container-> recycler view;error-layout;empty-layout;loading-layout */
        val genreRecyclerView = RecyclerView(frameLayoutContainer.context).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
            val animation=AnimationUtils.loadLayoutAnimation(this.context, R.anim.layout_animation_scale_in)
            layoutAnimation= animation
            layoutManager = gridLayoutManager(2)
            adapter = comicsByGenreAdapter
            setHasFixedSize(true)
            visibility = View.GONE
        }
        frameLayoutContainer.addView(genreRecyclerView)
        val errorEmptyLayout = ConstraintLayout(frameLayoutContainer.context).apply {
            id= ViewCompat.generateViewId()
            layoutParams=FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
            background = resourcesInstance.getDrawable(R.color.color_surface,null)

        }

        frameLayoutContainer.addView(errorEmptyLayout)

        val errorImageView = AppCompatImageView(errorEmptyLayout.context).apply{
            id= ViewCompat.generateViewId()
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(R.drawable.no_internet_connection_image)
            contentDescription= getString(R.string.error_image)
        }

        errorEmptyLayout.addView(errorImageView)
        val emptyErrorStateTitle = AppCompatTextView(errorEmptyLayout.context).apply {
            id = ViewCompat.generateViewId()
            gravity= Gravity.CENTER
            textSize = 16f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.SANS_SERIF
            text = "Nothing to see here.."

        }
        errorEmptyLayout.addView(emptyErrorStateTitle)

        val emptyErrorStateSubtitle = AppCompatTextView(errorEmptyLayout.context).apply {
            id = ViewCompat.generateViewId()
            gravity=Gravity.CENTER
            textSize = 14f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            text="Try searching for something"
        }
        errorEmptyLayout.addView(emptyErrorStateSubtitle)

        val errorEmptyLayoutConstraintSet = ConstraintSet()
        errorEmptyLayoutConstraintSet.clone(errorEmptyLayout)

        errorEmptyLayoutConstraintSet.constrainWidth(emptyErrorStateTitle.id,0)
        errorEmptyLayoutConstraintSet.constrainHeight(emptyErrorStateTitle.id,ConstraintSet.WRAP_CONTENT)



        errorEmptyLayoutConstraintSet.constrainHeight(errorImageView.id,180)
        errorEmptyLayoutConstraintSet.constrainWidth(errorImageView.id,180)


        errorEmptyLayoutConstraintSet.constrainWidth(emptyErrorStateSubtitle.id,0)
        errorEmptyLayoutConstraintSet.constrainHeight(emptyErrorStateSubtitle.id, ConstraintSet.WRAP_CONTENT)

        errorEmptyLayoutConstraintSet.setMargin(emptyErrorStateSubtitle.id,ConstraintSet.TOP,24)

        errorEmptyLayoutConstraintSet.connect(errorImageView.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(errorImageView.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(errorImageView.id,ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        errorEmptyLayoutConstraintSet.connect(errorImageView.id,ConstraintSet.BOTTOM, emptyErrorStateTitle.id, ConstraintSet.TOP)

        errorEmptyLayoutConstraintSet.connect(emptyErrorStateSubtitle.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(emptyErrorStateSubtitle.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(emptyErrorStateSubtitle.id,ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        errorEmptyLayoutConstraintSet.connect(emptyErrorStateSubtitle.id,ConstraintSet.TOP,emptyErrorStateTitle.id, ConstraintSet.BOTTOM)

        errorEmptyLayoutConstraintSet.connect(emptyErrorStateTitle.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        errorEmptyLayoutConstraintSet.connect(emptyErrorStateTitle.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        errorEmptyLayoutConstraintSet.connect(emptyErrorStateTitle.id, ConstraintSet.TOP,errorImageView.id, ConstraintSet.BOTTOM)
        errorEmptyLayoutConstraintSet.connect(emptyErrorStateTitle.id, ConstraintSet.BOTTOM,emptyErrorStateSubtitle.id, ConstraintSet.TOP)


        errorEmptyLayoutConstraintSet.applyTo(errorEmptyLayout)

        val loadingLayout = LoadingLayout(frameLayoutContainer.context).apply {
            id=ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
            visibility=View.GONE
        }
        frameLayoutContainer.addView(loadingLayout)

        return parentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resolveFilterFragmentInstance()

        /* Listen to/collect data in this lifecycle scope  */
        launchAndRepeatWithViewLifecycle {

        }
    }
    private fun onComicClicked(comicItem: ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private fun resolveFilterFragmentInstance():ComicFilterFragment?{
        return childFragmentManager.findFragmentById(R.id.filter_sheet) as? ComicFilterFragment
    }

    override fun onDestroy() {
        super.onDestroy()
        _comicsByGenreBinding = null
    }

}
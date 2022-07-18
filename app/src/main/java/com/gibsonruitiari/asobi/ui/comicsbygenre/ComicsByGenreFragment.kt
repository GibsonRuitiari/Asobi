package com.gibsonruitiari.asobi.ui.comicsbygenre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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
import com.gibsonruitiari.asobi.utilities.StatusBarScrimBehavior
import com.gibsonruitiari.asobi.utilities.extensions.loadPhotoUrl
import com.google.android.material.appbar.AppBarLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("UNCHECKED_CAST")
class ComicsByGenreFragment: MainNavigationFragment() {
    private var _comicsByGenreBinding:ComicsByGenreFragmentBinding?=null
    private val comicsByGenreBinding:ComicsByGenreFragmentBinding get() = _comicsByGenreBinding!!
    private val mainActivityViewModel:MainActivityViewModel by viewModel()
//    private val comicsByGenreAdapter =  composedPagedAdapter(createViewHolder = { viewGroup: ViewGroup, _: Int ->
//        viewGroup.viewHolderFrom(ComicItemLayoutBinding::inflate).apply {
//            itemView.setOnClickListener { onComicClicked(item) }
//        }
//    }, bindViewHolder = { viewHolder: RecyclerView.ViewHolder, item: ViewComics?, _ ->
//        (viewHolder as BindingViewHolder<ComicItemLayoutBinding>).bind(item)
//    })
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
        val constraintLayout = ConstraintLayout(parentContainer.context).apply {
            id = ViewCompat.generateViewId()
            layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
        }
        parentContainer.addView(constraintLayout)
        val helloTextView = AppCompatTextView(constraintLayout.context).apply {
            id = ViewCompat.generateViewId()
            text=getString(R.string.comics_by_genre)
           // layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams(0,0))
        }
        constraintLayout.addView(helloTextView)


        val set= ConstraintSet()
        set.clone(constraintLayout)

        set.constrainWidth(helloTextView.id,ConstraintSet.WRAP_CONTENT)
        set.constrainHeight(helloTextView.id,ConstraintSet.WRAP_CONTENT)

        set.connect(helloTextView.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(helloTextView.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(helloTextView.id,ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(helloTextView.id,ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.applyTo(constraintLayout)

        return parentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun onComicClicked(comicItem: ViewComics){
        Toast.makeText(requireContext(),"${comicItem.comicLink} clicked", Toast.LENGTH_SHORT).show()
    }
    private fun resolveFilterFragmentInstance():ComicFilterFragment{
        return childFragmentManager.findFragmentById(R.id.filter_sheet) as ComicFilterFragment
    }

    override fun onDestroy() {
        super.onDestroy()
        _comicsByGenreBinding = null
    }

}
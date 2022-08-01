package com.gibsonruitiari.asobi.ui.comicssearch


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.databinding.FragmentSearchBinding
import com.gibsonruitiari.asobi.databinding.GenreComicItemBinding
import com.gibsonruitiari.asobi.ui.comicsadapters.BindingViewHolder
import com.gibsonruitiari.asobi.ui.comicsadapters.listAdapterOf
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderDelegate
import com.gibsonruitiari.asobi.ui.comicsadapters.viewHolderFrom
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import com.gibsonruitiari.asobi.ui.uiModels.UiGenreModel
import com.gibsonruitiari.asobi.utilities.extensions.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsSearchFragment:Fragment() {
    private val comicsSearchViewModel: ComicsSearchViewModel by viewModel()
    private val comicsByGenreViewModel:ComicsByGenreViewModel by viewModel()
    private var comicsSearchFragmentBinding: FragmentSearchBinding? = null
    private val fragmentBinding
    get() = comicsSearchFragmentBinding!!
    private var loadingJob:Job?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        comicsSearchFragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null) applyWindowInsetsToParent()
        setUpMainFragmentRecyclerView()
        loadData(isHidden)
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
        fragmentBinding.root.apply {
            postDelayed({fragmentBinding.root.requestApplyInsetsWhenAttached()},500)
        }
    }
    private fun setUpMainFragmentRecyclerView(){
        val screenWidth= resourcesInstance().displayMetrics.run {
            widthPixels/density }
        with(fragmentBinding.genresRecyclerview){
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
            /*By default the medium density is 160f so we minus 4 just increase to accommodate smaller screens and come up with a proper
            * no of span count for our grid layout */
            this.layoutManager = this.gridLayoutManager(spanCount = (screenWidth/156f).toInt())
        }
}
    override fun onDestroy() {
        super.onDestroy()
        comicsSearchFragmentBinding = null
    }
    private fun showKeyboard(view: View) {
        WindowInsetsControllerCompat(requireActivity().window,view).show(WindowInsetsCompat.Type.ime())
    }

    private fun dismissKeyboard(view: View) {
        WindowInsetsControllerCompat(requireActivity().window,view).hide(WindowInsetsCompat.Type.ime())
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
                comicsByGenreViewModel.setGenre(genres.filterToGenre())
                // open genre : switch fragments maybe? haha
                doActionIfWeAreOnDebug { fragmentBinding.root.showSnackBar("${genres.genreName} clicked") }
            }
        }
    }, viewHolderBinder = {holder: BindingViewHolder<GenreComicItemBinding>, item: UiGenreModel, _: Int ->
        holder.bindComicGenres(item)
    })
    private fun UiGenreModel.filterToGenre():Genres = Genres.values().first { it.genreName ==genreName}

}
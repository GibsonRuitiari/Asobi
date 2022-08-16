package com.gibsonruitiari.asobi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreScreen
import com.gibsonruitiari.asobi.ui.comicssearch.ComicsGenreScreen
import com.gibsonruitiari.asobi.ui.completedcomics.CompletedComicsFragment
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverFragment
import com.gibsonruitiari.asobi.ui.latestcomics.LatestComicsFragment
import com.gibsonruitiari.asobi.ui.ongoingcomics.OngoingComicsFragment
import com.gibsonruitiari.asobi.ui.popularcomics.PopularComicsFragment
import com.gibsonruitiari.asobi.utilities.extensions.cancelIfActive
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ContainerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeScreen:Fragment() {
    private val logger: Logger by inject()
    private val mainFragmentViewModel: MainActivityViewModel by sharedViewModel()
    private lateinit var fragmentContainerView: FragmentContainerView
    private  var discoverFragment: DiscoverFragment?=null
    private  var latestComicsFragment:LatestComicsFragment?=null
    private  var ongoingComicsFragment:OngoingComicsFragment?=null
    private  var comicsByGenreScreen:ComicsByGenreScreen?=null
    private  var popularComicsFragment:PopularComicsFragment?=null
    private  var completedComicsFragment:CompletedComicsFragment?=null
    private var currentFragmentIndex = discoverFragmentIndex
    private var navigationEventsJob:Job?=null

    companion object{
        private const val discoverFragmentTag ="discover fragment"
        private const val latestComicsFragmentTag ="latest comics fragment"
        private const val ongoingComicsFragmentTag ="ongoing comics fragment"
        private const val popularComicsFragmentTag ="popular comics fragment"
        private const val completedComicsFragmentTag ="completed comics fragment"
        private const val comicsByGenreFragmentTag ="genre comics fragment"
        private const val currentFragmentIndexKey ="current fragment"
        private const val discoverFragmentIndex=0
        private const val latestComicsFragmentIndex=1
        private const val ongoingComicsFragmentIndex=2
        private const val genreComicsFragmentIndex=3
        private const val popularComicsFragmentIndex=4
        private const val completedComicsFragmentIndex=5
    }
    private val onBackPressedCallback = object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            when(val currentFragment=getFragmentFromIndex(currentFragmentIndex)){
                is DiscoverFragment->{
                    isEnabled=false
                    activity?.onBackPressed()
                    doActionIfWeAreOnDebug { logger.i("[return to main activity main activity on back press is called ? ]") }
                }
                else->{
                    childFragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .show(discoverFragment!!)
                        .commit()
                    currentFragmentIndex = discoverFragmentIndex
                    doActionIfWeAreOnDebug { logger.i("[set the current fragment to discover fragment ]") }
                }
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(currentFragmentIndexKey,currentFragmentIndex)
        childFragmentManager.putFragment(outState, discoverFragmentTag,discoverFragment!!)
        childFragmentManager.putFragment(outState, latestComicsFragmentTag,latestComicsFragment!!)
        childFragmentManager.putFragment(outState, ongoingComicsFragmentTag,ongoingComicsFragment!!)
        childFragmentManager.putFragment(outState, popularComicsFragmentTag,popularComicsFragment!!)
        childFragmentManager.putFragment(outState, completedComicsFragmentTag,completedComicsFragment!!)
        childFragmentManager.putFragment(outState, comicsByGenreFragmentTag,comicsByGenreScreen!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        if (savedInstanceState==null){
            discoverFragment=DiscoverFragment()
            latestComicsFragment=LatestComicsFragment()
            ongoingComicsFragment=OngoingComicsFragment()
            popularComicsFragment= PopularComicsFragment()
            comicsByGenreScreen= ComicsByGenreScreen()
            completedComicsFragment= CompletedComicsFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainFragmentView =ContainerView(requireContext())
        fragmentContainerView = mainFragmentView.fragmentContainerView
        if (savedInstanceState==null){
            childFragmentManager.beginTransaction()
                .add(fragmentContainerView.id, discoverFragment!!, discoverFragmentTag).show(discoverFragment!!)
                .add(fragmentContainerView.id, latestComicsFragment!!, latestComicsFragmentTag).hide(latestComicsFragment!!)
                .add(fragmentContainerView.id, ongoingComicsFragment!!, ongoingComicsFragmentTag).hide(ongoingComicsFragment!!)
                .add(fragmentContainerView.id, popularComicsFragment!!, popularComicsFragmentTag).hide(popularComicsFragment!!)
                .add(fragmentContainerView.id, completedComicsFragment!!, completedComicsFragmentTag).hide(completedComicsFragment!!)
                .add(fragmentContainerView.id, comicsByGenreScreen!!, comicsByGenreFragmentTag).hide(comicsByGenreScreen!!)
                .commit()
        }
       return mainFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(isHidden)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        loadData(hidden)
    }
    private fun loadData(isHidden:Boolean){
        if (isHidden){
            navigationEventsJob.cancelIfActive()
        }else{
            observeNavigationEventsFromViewModel()
        }
    }
    private fun observeNavigationEventsFromViewModel(){
        val currentFragment = getFragmentFromIndex(currentFragmentIndex)
        navigationEventsJob?.cancel()
        navigationEventsJob=viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                mainFragmentViewModel.navigationEvents.collectLatest{
                    when(it){
                        MainFragmentNavigationAction.NavigateToDiscoverScreen -> {
                        childFragmentManager.beginTransaction()
                            .hide(getFragmentFromIndex(currentFragmentIndex))
                            .show(discoverFragment!!)
                            .commit()
                        currentFragmentIndex= discoverFragmentIndex
                    }
                    MainFragmentNavigationAction.NavigateToLatestComicsScreen -> {
                        childFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(latestComicsFragment!!)
                            .commit()
                        currentFragmentIndex= latestComicsFragmentIndex
                        doActionIfWeAreOnDebug { logger.i("current fragment index is $currentFragment is latest frag hidden? ${latestComicsFragment!!.isHidden}") }

                    }
                    MainFragmentNavigationAction.NavigateToOngoingComicsScreen -> {
                        childFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(ongoingComicsFragment!!)
                            .commit()
                        currentFragmentIndex= ongoingComicsFragmentIndex
                    }
                        MainFragmentNavigationAction.NavigateToComicsByGenreScreen -> {
                            childFragmentManager.beginTransaction()
                                .hide(currentFragment)
                                .show(comicsByGenreScreen!!)
                                .commit()
                            currentFragmentIndex= genreComicsFragmentIndex
                        }
                        MainFragmentNavigationAction.NavigateToCompletedComicsScreen ->{
                            childFragmentManager.beginTransaction()
                                .hide(currentFragment)
                                .show(completedComicsFragment!!)
                                .commit()
                            currentFragmentIndex= completedComicsFragmentIndex
                        }
                        MainFragmentNavigationAction.NavigateToPopularComicsScreen -> {
                            childFragmentManager.beginTransaction()
                                .hide(currentFragment)
                                .show(popularComicsFragment!!)
                                .commit()
                            currentFragmentIndex= popularComicsFragmentIndex
                        }
                    }

                }
            }
        }

    }
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        /* If the saved instance is null it means the fragment is being created for the first time so handle the initialization on onCreateView */
        if (savedInstanceState==null || discoverFragment==null) return
        currentFragmentIndex= savedInstanceState.getInt(currentFragmentIndexKey)
        ongoingComicsFragment = childFragmentManager.findFragmentByTag(ongoingComicsFragmentTag) as OngoingComicsFragment
        discoverFragment = childFragmentManager.findFragmentByTag(discoverFragmentTag) as DiscoverFragment
        latestComicsFragment = childFragmentManager.findFragmentByTag(latestComicsFragmentTag) as LatestComicsFragment
        popularComicsFragment = childFragmentManager.findFragmentByTag(popularComicsFragmentTag) as PopularComicsFragment
        comicsByGenreScreen = childFragmentManager.findFragmentByTag(comicsByGenreFragmentTag) as ComicsByGenreScreen
        completedComicsFragment = childFragmentManager.findFragmentByTag(completedComicsFragmentTag) as CompletedComicsFragment
        val currentFragment = getFragmentFromIndex(currentFragmentIndex)
        childFragmentManager.beginTransaction()
            .hide(discoverFragment!!)
            .hide(latestComicsFragment!!)
            .hide(ongoingComicsFragment!!)
            .hide(comicsByGenreScreen!!)
            .hide(completedComicsFragment!!)
            .hide(popularComicsFragment!!)
            .show(currentFragment)
            .commit()
    }
    private fun getFragmentFromIndex(currentIndex:Int):Fragment= when (currentIndex) {
        discoverFragmentIndex -> discoverFragment!!
        latestComicsFragmentIndex -> latestComicsFragment!!
        ongoingComicsFragmentIndex -> ongoingComicsFragment!!
        completedComicsFragmentIndex->completedComicsFragment!!
        genreComicsFragmentIndex->comicsByGenreScreen!!
        popularComicsFragmentIndex->popularComicsFragment!!
        else -> {
            doActionIfWeAreOnDebug { logger.e("an unrecognized index was used $currentIndex") }
            throw IllegalStateException("unrecognized index $currentIndex")
        }
    }

}
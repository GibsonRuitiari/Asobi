package com.gibsonruitiari.asobi.ui.comicssearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.SearchScreenNavigationAction
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreScreen
import com.gibsonruitiari.asobi.utilities.extensions.cancelIfActive
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.gibsonruitiari.asobi.utilities.views.ContainerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * ComicSearchScreen?Lack of a better word man
 * This fragment essentially houses fragments that will be shown in comic search screen
 *
 */
class ComicsSearchScreen:Fragment() {
    private val logger:Logger by inject()
    private val mainFragmentViewModel:MainActivityViewModel by viewModel()
    private lateinit var fragmentContainerView:FragmentContainerView
    private var comicsGenreScreen:ComicsGenreScreen?=null
    private var comicsByGenreScreen:ComicsByGenreScreen?=null
    private var comicsSearchResultScreen:ComicsSearchResultsScreen?=null

    private var currentFragmentIndex= comicsGenreScreenIndex
    private var navigationEventsJob:Job?=null
    companion object{
        private const val comicsGenreScreenTag ="comics genre screen tag"
        private const val comicsByGenreFragmentTag ="comics by genre fragment tag"
        private const val currentFragmentIndexTag="current fragment tag"
        private const val comicsSearchResultsScreenTag ="comics search results screen tag"
        private const val comicsGenreScreenIndex=0
        private const val comicsByGenreFragmentIndex=1
        private const val comicsSearchResultScreenIndex =2
    }
    private val onBackPressedCallback = object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            val currentFragment=getFragmentFromIndex(currentFragmentIndex)
            if (currentFragment!=comicsGenreScreen){
                childFragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .show(comicsGenreScreen!!)
                    .commit()
            }else{
                isEnabled=false
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        if (savedInstanceState==null){
            comicsGenreScreen = ComicsGenreScreen()
            comicsByGenreScreen= ComicsByGenreScreen()
            comicsSearchResultScreen = ComicsSearchResultsScreen()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val comicsSearchScreenFragmentView = ContainerView(requireContext())
        fragmentContainerView = comicsSearchScreenFragmentView.fragmentContainerView
        if (savedInstanceState==null){
            childFragmentManager.beginTransaction()
                .add(fragmentContainerView.id,comicsGenreScreen!!, comicsGenreScreenTag).show(comicsGenreScreen!!)
                .add(fragmentContainerView.id,comicsByGenreScreen!!, comicsByGenreFragmentTag).hide(comicsByGenreScreen!!)
                .add(fragmentContainerView.id,comicsSearchResultScreen!!,comicsSearchResultsScreenTag).hide(comicsSearchResultScreen!!)
                .commit()
        }
        return comicsSearchScreenFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(isHidden)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(currentFragmentIndexTag,currentFragmentIndex)
        childFragmentManager.putFragment(outState, comicsGenreScreenTag,comicsGenreScreen!!)
        childFragmentManager.putFragment(outState, comicsByGenreFragmentTag,comicsByGenreScreen!!)
        childFragmentManager.putFragment(outState, comicsSearchResultsScreenTag,comicsSearchResultScreen!!)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        loadData(hidden)
    }
    private fun loadData(isHidden:Boolean){
        if (isHidden) navigationEventsJob.cancelIfActive()
        else{
            observeNavigationEventsFromMainViewModel()
        }
    }
    private fun observeNavigationEventsFromMainViewModel(){
        navigationEventsJob?.cancel()
        navigationEventsJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                mainFragmentViewModel.comicsSearchScreenNavigationEvents.collectLatest {
                    doActionIfWeAreOnDebug { logger.i("collection of navigation events started") }
                    when(it){
                        SearchScreenNavigationAction.NavigateToComicsByGenreFragmentScreen ->{
                            doActionIfWeAreOnDebug { logger.i("navigating to comics by genre  screen") }
                            childFragmentManager.beginTransaction()
                                .setTransition(TRANSIT_FRAGMENT_FADE)
                                .hide(getFragmentFromIndex(currentFragmentIndex))
                                .show(comicsByGenreScreen!!)
                                .commit()
                            currentFragmentIndex = comicsByGenreFragmentIndex
                        }
                        SearchScreenNavigationAction.NavigateToComicsGenreScreen -> {
                            doActionIfWeAreOnDebug { logger.i("navigating to comics genre screen") }
                            childFragmentManager.beginTransaction()
                                .setTransition(TRANSIT_FRAGMENT_FADE)
                                .hide(getFragmentFromIndex(currentFragmentIndex))
                                .show(comicsGenreScreen!!)
                                .commit()
                            currentFragmentIndex = comicsGenreScreenIndex
                        }
                        SearchScreenNavigationAction.NavigateToComicsSearchResultScreen ->{
                            doActionIfWeAreOnDebug { logger.i("navigating to comics search result screen") }
                            childFragmentManager.beginTransaction()
                                .setTransition(TRANSIT_FRAGMENT_FADE)
                                .hide(getFragmentFromIndex(currentFragmentIndex))
                                .show(comicsSearchResultScreen!!)
                                .commit()
                            currentFragmentIndex = comicsSearchResultScreenIndex
                        }
                    }
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState==null || comicsGenreScreen==null) return
        currentFragmentIndex = savedInstanceState.getInt(currentFragmentIndexTag)
        comicsGenreScreen = childFragmentManager.findFragmentByTag(comicsGenreScreenTag) as ComicsGenreScreen
        comicsByGenreScreen = childFragmentManager.findFragmentByTag(comicsByGenreFragmentTag) as ComicsByGenreScreen
        comicsSearchResultScreen = childFragmentManager.findFragmentByTag(comicsSearchResultsScreenTag) as ComicsSearchResultsScreen
        val currentFragment = getFragmentFromIndex(currentFragmentIndex)
        childFragmentManager.beginTransaction()
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .hide(comicsGenreScreen!!)
            .hide(comicsByGenreScreen!!)
            .hide(comicsSearchResultScreen!!)
            .show(currentFragment)
            .commit()
    }
    private fun getFragmentFromIndex(currentIndex:Int):Fragment = when(currentIndex){
        comicsByGenreFragmentIndex-> comicsByGenreScreen!!
        comicsGenreScreenIndex -> comicsGenreScreen!!
        comicsSearchResultScreenIndex -> comicsSearchResultScreen!!
        else ->{
            doActionIfWeAreOnDebug { logger.e("an unrecognized index given $currentIndex") }
            throw IllegalArgumentException("unrecognized index $currentIndex")
        }
    }
}
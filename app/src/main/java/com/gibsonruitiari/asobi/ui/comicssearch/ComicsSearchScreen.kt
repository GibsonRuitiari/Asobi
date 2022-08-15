package com.gibsonruitiari.asobi.ui.comicssearch

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

/**
 * ComicSearchScreen?Lack of a better word man
 * This fragment essentially houses fragments that will be shown in comic search screen
 *
 */
class ComicsSearchScreen:Fragment() {
    private val logger:Logger by inject()
    private val mainFragmentViewModel:MainActivityViewModel by sharedViewModel()
    private lateinit var fragmentContainerView:FragmentContainerView
    private var comicsGenreScreen:ComicsGenreScreen?=null
    private var comicsByGenreScreen:ComicsByGenreScreen?=null
    private var currentFragmentIndex= comicsGenreScreenIndex
    private var navigationEventsJob:Job?=null
    companion object{
        private const val comicsGenreScreenTag ="comics genre screen tag"
        private const val comicsByGenreFragmentTag ="comics by genre fragment tag"
        private const val currentFragmentIndexTag="current fragment tag"
        private const val comicsGenreScreenIndex=0
        private const val comicsByGenreFragmentIndex=1
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
        childFragmentManager.putFragment(outState, comicsGenreScreenTag,
        comicsGenreScreen!!)
        childFragmentManager.putFragment(outState, comicsByGenreFragmentTag,
        comicsByGenreScreen!!)

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
                    when(it){
                        SearchScreenNavigationAction.NavigateToComicsByGenreFragmentScreen ->{
                            childFragmentManager.beginTransaction()
                                .hide(getFragmentFromIndex(currentFragmentIndex))
                                .show(comicsByGenreScreen!!)
                                .commit()
                            currentFragmentIndex = comicsByGenreFragmentIndex
                        }
                        SearchScreenNavigationAction.NavigateToComicsGenreScreen -> {
                            childFragmentManager.beginTransaction()
                                .hide(getFragmentFromIndex(currentFragmentIndex))
                                .show(comicsGenreScreen!!)
                                .commit()
                            currentFragmentIndex = comicsGenreScreenIndex
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
        val currentFragment = getFragmentFromIndex(currentFragmentIndex)
        childFragmentManager.beginTransaction()
            .hide(comicsGenreScreen!!)
            .hide(comicsByGenreScreen!!)
            .show(currentFragment)
            .commit()
    }
    private fun getFragmentFromIndex(currentIndex:Int):Fragment = when(currentIndex){
        comicsByGenreFragmentIndex-> comicsByGenreScreen!!
        comicsGenreScreenIndex -> comicsGenreScreen!!
        else ->{
            doActionIfWeAreOnDebug { logger.e("an unrecognized index given $currentIndex") }
            throw IllegalArgumentException("unrecognized index $currentIndex")
        }
    }
}
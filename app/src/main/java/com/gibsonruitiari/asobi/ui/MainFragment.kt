package com.gibsonruitiari.asobi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverFragment
import com.gibsonruitiari.asobi.ui.latestcomics.LatestComicsFragment
import com.gibsonruitiari.asobi.ui.ongoingcomics.OngoingComicsFragment
import com.gibsonruitiari.asobi.utilities.extensions.cancelIfActive
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.extensions.setFragmentToBeShownToTheUser
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment:Fragment() {
    private val logger: Logger by inject()
    private val mainFragmentViewModel: MainActivityViewModel by viewModel()
    private lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var discoverFragment: DiscoverFragment
    private lateinit var latestComicsFragment:LatestComicsFragment
    private lateinit var ongoingComicsFragment:OngoingComicsFragment
    private var currentFragmentIndex = discoverFragmentIndex
    private var navigationEventsJob:Job?=null
    private var isFragmentHidden:Boolean=true
    companion object{
        private const val discoverFragmentTag ="discover fragment"
        private const val latestComicsFragmentTag ="latest comics fragment"
        private const val ongoingComicsFragmentTag ="ongoing comics fragment"
        private const val currentFragmentIndexKey ="current fragment"
        private const val isFragmentHiddenTag="isMainFragmentHiddenTag"
        private const val discoverFragmentIndex=0
        private const val latestComicsFragmentIndex=1
        private const val ongoingComicsFragmentIndex=2
    }
    private val onBackPressedCallback = object :OnBackPressedCallback(false){
        override fun handleOnBackPressed() {
            val currentFragment=getFragmentFromIndex(currentFragmentIndex)
            if (currentFragment!=discoverFragment){
                mainFragmentViewModel.openDiscoverScreen()
            }else requireActivity().onBackPressed()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(currentFragmentIndexKey,currentFragmentIndex)
        outState.putBoolean(isFragmentHiddenTag,isFragmentHidden)
        childFragmentManager.putFragment(outState, discoverFragmentTag,discoverFragment)
        childFragmentManager.putFragment(outState, latestComicsFragmentTag,latestComicsFragment)
        childFragmentManager.putFragment(outState, ongoingComicsFragmentTag,ongoingComicsFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        // if it is hidden==true
        doActionIfWeAreOnDebug {  logger.i("is main fragment hidden? $hidden")}
        isFragmentHidden=hidden
        loadData()
    }
    private fun loadData(){
        if (!isFragmentHidden){
            // initialize jobs
            discoverFragment.onHiddenChanged(!isFragmentHidden)
            observeNavigationEventsFromViewModel()

        }else{
            navigationEventsJob.cancelIfActive()
        }
    }
    private fun observeNavigationEventsFromViewModel(){
        navigationEventsJob?.cancel()
        navigationEventsJob=launchAndRepeatWithViewLifecycle {
            mainFragmentViewModel.navigationEvents.collectLatest {
                val currentFragment = getFragmentFromIndex(currentFragmentIndex)
                when(it){
                    MainFragmentNavigationAction.NavigateToDiscoverScreen -> {
                        childFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(discoverFragment)
                            .commit()
                        currentFragmentIndex= discoverFragmentIndex
                    }
                    MainFragmentNavigationAction.NavigateToLatestComicsScreen -> {
                        childFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(latestComicsFragment)
                            .commit()
                        currentFragmentIndex= latestComicsFragmentIndex
                    }
                    MainFragmentNavigationAction.NavigateToOngoingComicsScreen -> {
                        childFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(ongoingComicsFragment)
                            .commit()
                        currentFragmentIndex= ongoingComicsFragmentIndex
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainFragmentView =MainFragmentView(requireContext())
        fragmentContainerView = mainFragmentView.containerView
        if (savedInstanceState==null){
            doActionIfWeAreOnDebug {  logger.i("view state is being created for the first time")}
            discoverFragment=DiscoverFragment()
            latestComicsFragment=LatestComicsFragment()
            ongoingComicsFragment=OngoingComicsFragment()
            childFragmentManager.beginTransaction()
                .add(fragmentContainerView.id,discoverFragment, discoverFragmentTag)
                .add(fragmentContainerView.id, latestComicsFragment, latestComicsFragmentTag).hide(latestComicsFragment)
                .add(fragmentContainerView.id, ongoingComicsFragment, ongoingComicsFragmentTag).hide(latestComicsFragment)
                .commit()
           //currentFragmentIndex=0

        }
       return mainFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        /* If the saved instance is null it means the fragment is being created for the first time so handle the initialization on onCreateView */
        if (savedInstanceState==null) return
        doActionIfWeAreOnDebug {  logger.i("view state is being restored")}
        isFragmentHidden=savedInstanceState.getBoolean(isFragmentHiddenTag)
        currentFragmentIndex= savedInstanceState.getInt(currentFragmentIndexKey)
        ongoingComicsFragment = childFragmentManager.findFragmentByTag(ongoingComicsFragmentTag) as OngoingComicsFragment
        discoverFragment = childFragmentManager.findFragmentByTag(discoverFragmentTag) as DiscoverFragment
        latestComicsFragment = childFragmentManager.findFragmentByTag(latestComicsFragmentTag) as LatestComicsFragment
        val currentFragment = getFragmentFromIndex(currentFragmentIndex)
        childFragmentManager.beginTransaction()
            .hide(discoverFragment)
            .hide(latestComicsFragment)
            .hide(ongoingComicsFragment)
            .show(currentFragment)
            .commit()
        // trigger loading?
    }
    private fun getFragmentFromIndex(currentIndex:Int):Fragment= when (currentIndex) {
        discoverFragmentIndex -> discoverFragment
        latestComicsFragmentIndex -> latestComicsFragment
        ongoingComicsFragmentIndex -> ongoingComicsFragment
        else -> throw IllegalStateException("unrecognized index $currentIndex")
    }

}
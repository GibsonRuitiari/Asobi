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
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.extensions.setFragmentToBeShownToTheUser
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment:Fragment() {
    private val logger:Logger by inject()
    private val mainFragmentViewModel:MainActivityViewModel by viewModel()
    private val mainScreenFragments = ArrayList<Fragment>(4)
    private lateinit var discoverFragment: DiscoverFragment
    private lateinit var latestComicsFragment:LatestComicsFragment
    private lateinit var ongoingComicsFragment:OngoingComicsFragment
    private lateinit var fragmentContainerView: FragmentContainerView
    private var currentFragmentIndex =0
    companion object{
        private const val discoverFragmentTag ="discover fragment"
        private const val latestComicsFragmentTag ="latest comics fragment"
        private const val ongoingComicsFragmentTag ="ongoing comics fragment"
        private const val currentFragmentIndexKey ="current fragment"
    }
    private val onBackPressedCallback = object :OnBackPressedCallback(false){
        override fun handleOnBackPressed() {
            val currentFragment=mainScreenFragments[currentFragmentIndex]
            if (currentFragment!=discoverFragment){
                navigateTo(discoverFragment)
            }else requireActivity().onBackPressed()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState==null){
            discoverFragment=DiscoverFragment()
            latestComicsFragment=LatestComicsFragment()
            ongoingComicsFragment=OngoingComicsFragment()
            childFragmentManager.beginTransaction()
                .add(fragmentContainerView.id,discoverFragment, discoverFragmentTag)
                .add(fragmentContainerView.id, latestComicsFragment, latestComicsFragmentTag)
                .add(fragmentContainerView.id, ongoingComicsFragment, ongoingComicsFragmentTag)
                .commit()
            mainScreenFragments.add(discoverFragment)
            mainScreenFragments.add(latestComicsFragment)
            mainScreenFragments.add(ongoingComicsFragment)
        }else{
            currentFragmentIndex= savedInstanceState.getInt(currentFragmentIndexKey,0)
            ongoingComicsFragment = childFragmentManager.findFragmentByTag(ongoingComicsFragmentTag) as OngoingComicsFragment
            discoverFragment = childFragmentManager.findFragmentByTag(discoverFragmentTag) as DiscoverFragment
            latestComicsFragment = childFragmentManager.findFragmentByTag(latestComicsFragmentTag) as LatestComicsFragment

        }
        val currentFragment = mainScreenFragments[currentFragmentIndex]
        navigateTo(currentFragment)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

    }

    private fun navigateTo(fragment: Fragment){
        childFragmentManager.setFragmentToBeShownToTheUser(logger = logger,
            fragmentsArray = mainScreenFragments, selectedFragment = fragment){
            currentFragmentIndex = it
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(currentFragmentIndexKey,currentFragmentIndex)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            discoverFragment.onHiddenChanged(true)
            latestComicsFragment.onHiddenChanged(true)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val parentContainer=MainFragmentView(requireContext())
        fragmentContainerView=parentContainer.fragmentContainerView
        return parentContainer
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            mainFragmentViewModel.navigationEvents.collectLatest {
                when(it){
                    MainFragmentNavigationAction.NavigateToDiscoverScreen -> navigateTo(discoverFragment)
                    MainFragmentNavigationAction.NavigateToLatestComicsScreen -> navigateTo(latestComicsFragment)
                    MainFragmentNavigationAction.NavigateToOngoingComicsScreen -> navigateTo(ongoingComicsFragment)
                }
            }
        }
    }
}
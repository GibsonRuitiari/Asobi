package com.gibsonruitiari.asobi.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ActivityMainBinding
import com.gibsonruitiari.asobi.ui.comicssearch.ComicsSearchFragment
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverFragment
import com.gibsonruitiari.asobi.ui.userlibrary.UserLibrary
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.extensions.setFragmentToBeShownToTheUser
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView
import org.koin.android.ext.android.inject

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userLibraryFragment:UserLibrary
    private lateinit var mainFragment:MainFragment
    private lateinit var searchFragment:ComicsSearchFragment
    private val logger:Logger by inject()
    private val navigationBarViewFragments = ArrayList<Fragment>(3)
    private var selectedFragmentIndex =0
    private lateinit var navigationBarView: NavigationBarView
    companion object{
        private const val selectedIndexTag ="selected index"
        private const val discoverFragmentTag ="discover fragment tag"
        private const val searchFragmentTag ="search fragment tag"
        private const val userLibraryFragmentTag ="user library fragment tag"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentContainerId = binding.fragmentContainer.id
        if (savedInstanceState==null){
            /* first time initialization */
            mainFragment = MainFragment()
            searchFragment = ComicsSearchFragment()
            userLibraryFragment = UserLibrary()
            supportFragmentManager.beginTransaction()
                .add(fragmentContainerId,mainFragment, discoverFragmentTag)
                .add(fragmentContainerId,searchFragment, searchFragmentTag)
                .add(fragmentContainerId,userLibraryFragment, userLibraryFragmentTag)
                .commitNow()
            navigationBarViewFragments.add(mainFragment)
            navigationBarViewFragments.add(searchFragment)
            navigationBarViewFragments.add(userLibraryFragment)
        }else{
            selectedFragmentIndex = savedInstanceState.getInt(selectedIndexTag,0)
            mainFragment = supportFragmentManager.findFragmentByTag(discoverFragmentTag) as MainFragment
            searchFragment = supportFragmentManager.findFragmentByTag(searchFragmentTag) as ComicsSearchFragment
            userLibraryFragment = supportFragmentManager.findFragmentByTag(userLibraryFragmentTag) as UserLibrary
        }

        /* get an instance of navigation bar view, note: chances of both being null at the same time are one in a million*/
         navigationBarView = (binding.navRailView ?: binding.navigation) as NavigationBarView
        setUpNavigationBarViews()
        applyWindowInsetsOnStatusBarScrim()
        applyWindowInsetsOnRootContainer()
        val selectedFragment = navigationBarViewFragments[selectedFragmentIndex]
        changeFragment(selectedFragment)
        /* set up on click listeners for navigation bar view  */
        navigationBarView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.mainScreen ->{
                    doActionIfWeAreOnDebug { logger.i("main fragment screen selected;") }
                    changeFragment(mainFragment)
                    true
                }
                R.id.searchScreen->{
                    doActionIfWeAreOnDebug { logger.i("search screen selected;") }
                    changeFragment(searchFragment)
                    true
                }
                R.id.libraryScreen->{
                    doActionIfWeAreOnDebug{logger.i("library screen selected;")}
                    changeFragment(userLibraryFragment)
                    true
                }
                else->false
            }
        }
        /* prevent items from being reselected so don't implement anything here  */
        navigationBarView.setOnItemReselectedListener{}
    }


    private fun setUpNavigationBarViews(){
        binding.navRailView?.let {
            applyWindowInsetsOnNavigationRailView(it)
        }
    }
    private fun applyWindowInsetsOnNavigationRailView(navigationRailView: NavigationRailView){
        ViewCompat.setOnApplyWindowInsetsListener(navigationRailView){view,insets->
            // pad the navigation rail so the contents aren't drawn behind the sys ui bar
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top=systemBars.top,
                bottom = systemBars.bottom)
            insets
        }
    }
    private fun applyWindowInsetsOnStatusBarScrim(){
        binding.statusBarScrim.setOnApplyWindowInsetsListener { v, insets ->
            val topInset =insets.systemWindowInsetTop
            if (v.layoutParams.height != topInset) {
                v.layoutParams.height = topInset
                v.requestLayout()
            }
            insets
        }
    }
    private fun applyWindowInsetsOnRootContainer(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootContainer){view,insets->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            binding.navigation?.isVisible = !imeVisible
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomPadding = if (binding.navigation?.isVisible == true) systemBars.bottom else 0
            view.updatePadding(left=systemBars.left,
                right=systemBars.right, bottom = bottomPadding)
            // consume the insets
            WindowInsetsCompat.Builder(insets).setInsets(WindowInsetsCompat
                .Type.systemBars(), Insets.of(0,
                systemBars.top,0,systemBars.bottom- bottomPadding)).build()
        }
    }
    private fun changeFragment(fragment: Fragment){
        supportFragmentManager.setFragmentToBeShownToTheUser(logger = logger,
            fragmentsArray = navigationBarViewFragments, selectedFragment = fragment){
            selectedFragmentIndex = it
        }
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(selectedIndexTag,selectedFragmentIndex)
    }


}
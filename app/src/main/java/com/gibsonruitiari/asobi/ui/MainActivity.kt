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
import com.gibsonruitiari.asobi.ui.userlibrary.UserLibrary
import com.gibsonruitiari.asobi.utilities.logging.Logger
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userLibraryFragment:UserLibrary
    private lateinit var mainFragment:MainFragment
    private lateinit var searchFragment:ComicsSearchFragment
    private val logger:Logger by inject()
    private var selectedFragmentIndex = mainFragmentIndex
    private lateinit var navigationBarView: NavigationBarView
    private val mainActivityViewModel:MainActivityViewModel by viewModel()
    companion object{
        private const val selectedIndexTag ="selected index"
        private const val mainFragmentTag ="discover fragment tag"
        private const val searchFragmentTag ="search fragment tag"
        private const val userLibraryFragmentTag ="user library fragment tag"
        private const val mainFragmentIndex=0
        private const val searchFragmentIndex=1
        private const val userLibraryFragmentIndex=2
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
                .add(fragmentContainerId,mainFragment, mainFragmentTag).show(mainFragment)
                .add(fragmentContainerId,searchFragment, searchFragmentTag).hide(searchFragment)
                .add(fragmentContainerId,userLibraryFragment, userLibraryFragmentTag).hide(userLibraryFragment)
                .commitNow()

            mainActivityViewModel.setMainFragmentStatus(false)
        }else{
            selectedFragmentIndex = savedInstanceState.getInt(selectedIndexTag, mainFragmentIndex)
            mainFragment = supportFragmentManager.findFragmentByTag(mainFragmentTag) as MainFragment
            searchFragment = supportFragmentManager.findFragmentByTag(searchFragmentTag) as ComicsSearchFragment
            userLibraryFragment = supportFragmentManager.findFragmentByTag(userLibraryFragmentTag) as UserLibrary
            val currentFragment = getFragmentFromIndex(selectedFragmentIndex)
            supportFragmentManager.beginTransaction()
                .hide(mainFragment)
                .hide(searchFragment)
                .hide(userLibraryFragment)
                .show(currentFragment)
                .commit()
            mainActivityViewModel.setMainFragmentStatus(currentFragment==mainFragment)
        }
        /* get an instance of navigation bar view, note: chances of both being null at the same time are one in a million*/
         navigationBarView = (binding.navRailView ?: binding.navigation) as NavigationBarView

        setUpNavigationBarViews()
        applyWindowInsetsOnStatusBarScrim()
        applyWindowInsetsOnRootContainer()
        /* set up on click listeners for navigation bar view  */
        navigationBarView.setOnItemSelectedListener {
            val currentFragment = getFragmentFromIndex(selectedFragmentIndex)
            when(it.itemId){
                R.id.mainScreen ->{
                    supportFragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .show(mainFragment)
                        .commit()
                    selectedFragmentIndex= mainFragmentIndex
                    mainActivityViewModel.setMainFragmentStatus(false)
                    true
                }
                R.id.searchScreen->{
                    supportFragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .show(searchFragment)
                        .commit()
                    selectedFragmentIndex= searchFragmentIndex
                    mainActivityViewModel.setMainFragmentStatus(true)
                    true
                }
                R.id.libraryScreen->{
                    supportFragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .show(userLibraryFragment)
                        .commit()
                    selectedFragmentIndex= userLibraryFragmentIndex
                    mainActivityViewModel.setMainFragmentStatus(true)
                    true
                }
                else->false
            }
        }

    }

    private fun setUpNavigationBarViews(){
        navigationBarView.itemIconTintList=null
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
    private fun getFragmentFromIndex(currentIndex:Int):Fragment= when (currentIndex) {
        mainFragmentIndex -> mainFragment
        searchFragmentIndex -> searchFragment
        userLibraryFragmentIndex -> userLibraryFragment
        else -> throw IllegalStateException("unrecognized index $currentIndex")
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(selectedIndexTag,selectedFragmentIndex)
        supportFragmentManager.putFragment(outState, mainFragmentTag,mainFragment)
        supportFragmentManager.putFragment(outState, searchFragmentTag,searchFragment)
        supportFragmentManager.putFragment(outState, userLibraryFragmentTag,userLibraryFragment)
    }

    override fun onBackPressed() {
        val currentFragment = getFragmentFromIndex(selectedFragmentIndex)
        when{
            currentFragment!=mainFragment->{
                supportFragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .show(mainFragment)
                    .commit()
                selectedFragmentIndex= mainFragmentIndex
                mainActivityViewModel.setMainFragmentStatus(false)
            }
            else-> super.onBackPressed()
            }
    }

}
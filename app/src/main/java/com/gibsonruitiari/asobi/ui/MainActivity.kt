package com.gibsonruitiari.asobi.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.window.layout.WindowMetricsCalculator
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ActivityMainBinding
import com.gibsonruitiari.asobi.utilities.ScreenSize
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationHost {
    companion object{
        private const val NAV_ID_NONE=-1
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
        private val TOP_LEVEL_DESTINATIONS = setOf(R.id.navigation_comics_by_genre,
            R.id.navigation_completed_comics,R.id.navigation_latest_comics,
            R.id.navigation_popular_comics)
    }
    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModel()
    private var currentNavId = NAV_ID_NONE

    private lateinit var navController: NavController
    private lateinit var navHostFragment:NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val container:ViewGroup = binding.root
        /* https://issuetracker.google.com/202338815 hook a utility view to listen to configurationChanges */
        container.addView(object:View(this){
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                mainActivityViewModel.setScreenWidth(computeWindowSizeClasses())
            }
        })
        /* cannot initialize navController from FragmentContainerView since navController does not depend on fragment
         adding FragmentContainerView breaks code, so don't use it instead use fragment if you can (despite AS Warning)
          see issue https://issuetracker.google.com/issues/142847973 */
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        //navController = findNavController(R.id.nav_host_fragment_content_main)
        hookUpNavControllerToDestinationChangedListener()
        /* get an instance of navigation bar view, note: chances of both being null at the same time are one in a million*/
        val navigationBarView:NavigationBarView = (binding.navRailView ?: binding.navigation) as NavigationBarView
        NavigationUI.setupWithNavController(navigationBarView, navController)
        setUpNavigationBarViews()
        if (savedInstanceState==null){
            currentNavId = navController.graph.startDestinationId
            val requestedNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, currentNavId)
            navigateTo(requestedNavId)
        }
        applyWindowInsetsOnStatusBarScrim()
      applyWindowInsetsOnRootContainer()
    }
    private fun setUpNavigationBarViews(){
        binding.navigation?.let {
            it.setOnItemReselectedListener {  }
            it.setOnItemSelectedListener(navigationBarViewClickListener)

        }
        binding.navRailView?.let {
            it.setOnItemReselectedListener {  }
            it.setOnItemSelectedListener(navigationBarViewClickListener)
            applyWindowInsetsOnNavigationRailView(it)
        }

    }
    private fun hookUpNavControllerToDestinationChangedListener(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentNavId = destination.id
        }
    }
    private fun navigateTo(navigationId:Int){
        if (navigationId==currentNavId) return
        navController.navigate(navigationId)
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
    private val navigationBarViewClickListener = NavigationBarView.OnItemSelectedListener {
        when(it.itemId){
            R.id.menu_item_settings ->{
                it.isChecked = true
            //    navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                true
            }
            R.id.menu_item_home ->{
                it.isChecked = true

                true
            }
            R.id.menu_item_favorites ->{
                it.isChecked = true
                true
            }
            else-> false
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNavId = navController.currentDestination?.id ?: NAV_ID_NONE
    }
    private fun computeWindowSizeClasses():ScreenSize{
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val widthDp = metrics.bounds.width()/resources.displayMetrics.density
        return when{
            widthDp < 600f -> ScreenSize.COMPACT
            widthDp < 640f-> ScreenSize.MEDIUM
            else -> ScreenSize.EXPANDED
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        /* current fragment might be null so use as? when casting */
        val currentFragment=navHostFragment.childFragmentManager.primaryNavigationFragment as? MainNavigationFragment
        currentFragment?.onUserInteraction()

    }
    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS)
        toolbar.setupWithNavController(navController,appBarConfiguration)
    }

}
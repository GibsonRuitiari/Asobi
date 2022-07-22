package com.gibsonruitiari.asobi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView

class MainActivity : AppCompatActivity() {
    companion object{
        private const val NAV_ID_NONE=-1
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
    }
    private lateinit var binding: ActivityMainBinding
    private var currentNavId = NAV_ID_NONE
    private lateinit var navController: NavController
    private lateinit var navHostFragment:NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
      //  hookUpNavControllerToDestinationChangedListener()
        /* get an instance of navigation bar view, note: chances of both being null at the same time are one in a million*/
        val navigationBarView:NavigationBarView = (binding.navRailView ?: binding.navigation) as NavigationBarView
        // top level declarations
        findViewById<BottomNavigationView>(R.id.navigation).setupWithNavController(navController)

        setUpNavigationBarViews()
        setSupportActionBar(null)
        if (savedInstanceState==null){
            currentNavId = navController.graph.startDestinationId
            val requestedNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, currentNavId)
            navigateTo(requestedNavId)
        }
        findViewById<BottomNavigationView>(R.id.navigation).setOnItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it,navController)
        }

        applyWindowInsetsOnStatusBarScrim()
        applyWindowInsetsOnRootContainer()
        hookUpNavControllerToDestinationChangedListener()

    }
    private fun setUpNavigationBarViews(){
        binding.navRailView?.let {
            applyWindowInsetsOnNavigationRailView(it)
        }

    }
    private fun hookUpNavControllerToDestinationChangedListener(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            println("destination: ${destination.id}")
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNavId = navController.currentDestination?.id ?: NAV_ID_NONE
    }




}
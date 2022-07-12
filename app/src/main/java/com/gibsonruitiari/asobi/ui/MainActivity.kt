package com.gibsonruitiari.asobi.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.window.layout.WindowMetricsCalculator
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.ScreenSize
import com.gibsonruitiari.asobi.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val container:ViewGroup = binding.root
        // https://issuetracker.google.com/202338815
        // hook a utility view to listen to configurationChanges
        container.addView(object:View(this){
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                mainActivityViewModel.setScreenWidth(computeWindowSizeClasses())
            }
        })
        /* cannot initialize navController from FragmentContainerView since navController does not depend on fragment
         adding FragmentContainerView breaks code, so don't use it instead use fragment (despite AS Warning)
          see issue https://issuetracker.google.com/issues/142847973 */
        navController = findNavController(R.id.nav_host_fragment_content_main)
        /* get an instance of navigation bar view, note: chances of both being null at the same time are one in a million*/
        val navigationBarView:NavigationBarView = (binding.navRailView ?: binding.navigation) as NavigationBarView
        NavigationUI.setupWithNavController(navigationBarView, navController)
        binding.navRailView?.setOnItemSelectedListener (navigationBarViewClickListener)
        binding.navigation?.setOnItemSelectedListener(navigationBarViewClickListener)

        binding.navRailView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it){view,insets->
                // pad the navigation rail so the contents aren't drawn behind the sys ui bar
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(top=systemBars.top,
                bottom = systemBars.bottom)
                insets
            }
        }
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
    private fun computeWindowSizeClasses():ScreenSize{
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val widthDp = metrics.bounds.width()/resources.displayMetrics.density
        return when{
            widthDp < 600f -> ScreenSize.COMPACT
            widthDp < 640f-> ScreenSize.MEDIUM
            else -> ScreenSize.EXPANDED
        }

    }

}
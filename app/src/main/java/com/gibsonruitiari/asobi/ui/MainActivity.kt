package com.gibsonruitiari.asobi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.*
import com.gibsonruitiari.asobi.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navigationBarView: NavigationBarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* get an instance of navigation bar view, note: chances of both being null at the same time are one in a million*/
         navigationBarView = (binding.navRailView ?: binding.navigation) as NavigationBarView
        setUpNavigationBarViews()
        applyWindowInsetsOnStatusBarScrim()
        applyWindowInsetsOnRootContainer()

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






}
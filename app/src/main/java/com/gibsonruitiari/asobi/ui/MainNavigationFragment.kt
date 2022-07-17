package com.gibsonruitiari.asobi.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gibsonruitiari.asobi.R

/**
 * Fragment representing a main navigation destination. This class handles wiring up the [Toolbar]
 * navigation icon if the fragment is attached to a [NavigationHost].
 */
open class MainNavigationFragment:Fragment(),NavigationDestination {
    private var navigationHost:NavigationHost?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /* If we have a toolbar and we are attached to a proper navigation host, set up the toolbar
       navigation icon.*/
        val host = navigationHost ?: return
        val mainToolbar:Toolbar = view.findViewById(R.id.toolbar) ?: return
        mainToolbar.apply {
            host.registerToolbarWithNavigation(this)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost)
        navigationHost =context
    }

    override fun onDetach() {
        super.onDetach()
       navigationHost = null
    }
}
package com.gibsonruitiari.asobi.utilities.widgets

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.navigation.fragment.NavHostFragment

/* A nav host fragment that dispatches the insets to all its children (views under this subtree)
* During fragment transaction, the outgoing fragment may have consumed all the insets correctly
* but the incoming one may fail to consume those insets until the next onLayout() (layout)
* thus to ensure consistency, dispatch the insets to incoming fragments
* Equivalent of fitSystemWindows:true note however, if you are using coordinator layout you
* don't have to do this */
class InsetsNavHostFragment :NavHostFragment(){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnApplyWindowInsetsListener { v, insets ->
            (v as? ViewGroup)?.forEach {child->
                /* For each view present in this view group/container dispatch the insets */
                child.dispatchApplyWindowInsets(insets)
            }
            return@setOnApplyWindowInsetsListener insets
        }
    }
}
package com.gibsonruitiari.asobi.ui


/* To be implemented by components that host top-level navigation destinations  */
interface NavigationHost {
    /* Called by MainFragment to set up its toolbar with navigation controller */
    fun registerToolbarWithNavigation(toolbar: androidx.appcompat.widget.Toolbar)
}

package com.gibsonruitiari.asobi.ui

/*To be implemented by main navigation destinations  */
interface NavigationDestination {
    /*  called by the host when the user interacts with it */
    fun onUserInteraction(){}
}
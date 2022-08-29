package com.gibsonruitiari.asobi.ui.comicdetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.gibsonruitiari.asobi.utilities.views.ParentFragmentsView
import com.google.android.material.appbar.AppBarLayout

class ComicDetailsScreen:Fragment() {
    private lateinit var rootView:CoordinatorLayout
    inner class ComicDetailsScreenView(context:Context):ParentFragmentsView(context){

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = ComicDetailsScreenView(requireContext())
        return rootView
    }
}
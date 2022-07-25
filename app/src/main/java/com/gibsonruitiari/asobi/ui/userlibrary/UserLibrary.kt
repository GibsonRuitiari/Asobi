package com.gibsonruitiari.asobi.ui.userlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import com.gibsonruitiari.asobi.utilities.logging.Logger
import org.koin.android.ext.android.inject


class UserLibrary : Fragment() {
    /* By default fragment start out as not hidden but since we are first showing discover frag our default will be true */
    private var isFragmentHidden:Boolean=true
    private val logger:Logger by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState!=null){
           isFragmentHidden= savedInstanceState.getBoolean(isFragmentHiddenTag,true)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isFragmentHidden = hidden
        doActionIfWeAreOnDebug {  logger.i("is fragment hidden $hidden our variable $isFragmentHidden")}
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFragmentHiddenTag,isFragmentHidden)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_library, container, false)
    }
    companion object{
        private const val isFragmentHiddenTag ="isFragmentHidden"
    }



}
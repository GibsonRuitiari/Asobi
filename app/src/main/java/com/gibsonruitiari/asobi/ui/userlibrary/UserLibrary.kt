package com.gibsonruitiari.asobi.ui.userlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.utilities.extensions.doActionIfWeAreOnDebug
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class UserLibrary : Fragment() {
    /* By default fragment start out as not hidden but since we are first showing discover frag our default will be true */
    private var isFragmentHidden:Boolean=true
    private val logger:Logger by inject()
    private var job:Job?=null
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
        doLoadData(isFragmentHidden)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFragmentHiddenTag,isFragmentHidden)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    companion object{
        private const val isFragmentHiddenTag ="isFragmentHidden"
    }

    private fun doLoadData(isHidden:Boolean) {
        if (!isHidden) {
           initializeJobIfShown()
        } else {
            cancelJobIfItIsActive()
        }
    }


    private fun initializeJobIfShown(){
        job?.cancel() // cancel existing job
        logger.i("starting the job now")
        job=viewLifecycleOwner.lifecycleScope.launch{
            /* Coroutine will be automatically stopped whenever we approach onStop and started whenever we approach onStart()
            * so this saves us from cancelling and starting the job in onStop and onStart respectively  */
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                logger.i("hello from this coroutine scope")}
            }


    }
    private fun cancelJobIfItIsActive(){
        job?.let {
            if (it.isActive) {
                logger.i("killing the job")
                it.cancel()
                logger.i("is job active ${it.isActive}")
            }
        }
    }


}
package com.gibsonruitiari.asobi.ui.comicssearch


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gibsonruitiari.asobi.databinding.FragmentSearchBinding
import com.gibsonruitiari.asobi.utilities.extensions.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicsSearchFragment:Fragment() {
    private val comicsSearchViewModel:ComicsSearchViewModel by viewModel()
    private  var comicsSearchFragmentBinding:FragmentSearchBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        comicsSearchFragmentBinding=FragmentSearchBinding.inflate(inflater,container,false)
        val parentContainer = comicsSearchFragmentBinding!!.root
        return parentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            comicsSearchViewModel.observeState().collectLatest {
//              if (it.isLoading){
//                  println("loading please wait")
//              }else{
//                 if ( it.searchResults.errorMessage!=null){
//                     //println("we have an error:${it.searchResults.errorMessage}")
//                 }else{
//                     println("our data ${it.searchResults.searchResult}")
//                 }
//              }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        comicsSearchFragmentBinding = null
    }
}
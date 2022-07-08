package com.gibsonruitiari.asobi.presenter.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gibsonruitiari.asobi.common.utils.RecyclerViewItemDecoration
import com.gibsonruitiari.asobi.databinding.FragmentSwipeRecyclerviewBinding

abstract class BasePagedFragment<Model:Any,VH:RecyclerView.ViewHolder>:
Fragment(){
    private var _binding:FragmentSwipeRecyclerviewBinding?=null
    val binding get() = _binding!!
    abstract fun observeUiStates()
    abstract val pagedListAdapter:PagingDataAdapter<Model,VH>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSwipeRecyclerviewBinding.inflate(inflater,
        container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.baseFragRecyclerView.apply {
            adapter = pagedListAdapter



        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}
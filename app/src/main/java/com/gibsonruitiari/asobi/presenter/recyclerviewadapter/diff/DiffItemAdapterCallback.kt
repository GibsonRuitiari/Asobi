package com.gibsonruitiari.asobi.presenter.recyclerviewadapter.diff

import androidx.recyclerview.widget.DiffUtil

/**
 * Calculates the difference between two items in a recyclerview
 * Please note to use this adapter you ought to use Data class/ type T must be
 * a Data class
 */
class DiffItemAdapterCallback<T:Any>:DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
       return  if (oldItem is Differentiable && newItem is Differentiable) oldItem.diffId == newItem.diffId
        else false
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return  if (oldItem is Differentiable && newItem is Differentiable) oldItem.areContentsTheSame(newItem)
            else oldItem==newItem
    }

    override fun getChangePayload(oldItem: T, newItem: T): Any? {
        return  if (oldItem is Differentiable && newItem is Differentiable)oldItem.getChangePayload(newItem)
        else null
    }

}
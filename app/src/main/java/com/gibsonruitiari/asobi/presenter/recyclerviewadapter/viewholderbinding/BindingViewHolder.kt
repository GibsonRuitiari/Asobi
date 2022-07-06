package com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.map
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewDelegate
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * This delegate reads and writes properties to a [Map] the [Map] is held in a tag
 * associated with a view in this case the view is [RecyclerView.ViewHolder.itemView]
 */
fun <T> viewHolderDelegate(default:T?=null):ReadWriteProperty<RecyclerView.ViewHolder,T> = viewDelegate(default).map(mapper = RecyclerView.ViewHolder::itemView)
// open to extension or inheritance by other classes
open class BindingViewHolder<T:ViewBinding>(binding:T):RecyclerView.ViewHolder(binding.root){
    /* not everyone uses view binding so to make it flexible create a secondary constructor that delegates creation of view needed by view holder to the primary constructor  */
   constructor(viewGroup:ViewGroup,creator:(layoutInflater:LayoutInflater,
    attachToRoot:Boolean,root:ViewGroup)->T):this(creator(LayoutInflater.from(viewGroup.context),
    false,viewGroup))

}
// convenient method not a must though since we are using view binding
fun <T:ViewBinding>ViewGroup.createViewHolder(creator: (layoutInflater: LayoutInflater, attachToRoot: Boolean,
                                                                    root: ViewGroup) -> T) = BindingViewHolder(this,creator)

/** Tags enable us to put/associate views with some data (this is how views memorize their own data);
 *  thus we use getTag() and setTag(key,object) to set and get objects/data associated with a view
 *  The key should be unique to avoid collision: two views using the same key?
**/
inline fun <reified T> View.getOrPutTag(@IdRes key:Int, initializer:()->T):T{
  return  getTag(key) as T ?: initializer().also { setTag(key,it) }
}
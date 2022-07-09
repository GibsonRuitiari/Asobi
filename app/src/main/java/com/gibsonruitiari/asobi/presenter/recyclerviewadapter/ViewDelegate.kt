package com.gibsonruitiari.asobi.presenter.recyclerviewadapter

import android.view.View
import androidx.viewbinding.ViewBinding
import com.gibsonruitiari.asobi.R
import com.gibsonruitiari.asobi.presenter.recyclerviewadapter.viewholderbinding.getOrPutTag
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Responsible for mapping one delegate into another delegate
 * When we are given an input delegate we transform it to another delegate
 * Param In represents the type of object that owns input delegate, Param T represents
 * the type of the property value held by the input delegate
 * Param Out represents the type of object that owns the output delegate
 * Note: In both, the property value does not change what changes is the type of object that
 * owns the delegate only
 * Param postWrite represents an action that needs to be taken on both the objects
 * that own the input and output delegate
 */
private class MapperDelegate<In,Out,T>(private val inputDelegate:ReadWriteProperty<In,T>,
                                       private val postWrite:((Out,In)->Unit)?=null,
private val mapper:(Out)->In):ReadWriteProperty<Out,T>{
    override fun setValue(thisRef: Out, property: KProperty<*>, value: T) {
        val mapped = mapper(thisRef)
        inputDelegate.setValue(mapped,property, value)
        postWrite?.invoke(thisRef,mapped)
    }

    override fun getValue(thisRef: Out, property: KProperty<*>): T {
        return inputDelegate.getValue(mapper(thisRef), property)
    }
}
fun <In,Out,T> ReadWriteProperty<In,T>.map(postWrite: ((Out, In) -> Unit)?=null,
mapper: (Out) -> In):ReadWriteProperty<Out,T> = MapperDelegate(inputDelegate = this,
postWrite,mapper)


/**
 * Delegate the reading and writing of properties to a map
 * the map is then associated with a view tag, so whenever we want to access a certain property inside the map that are
 * associated with a view tag we just need to call this delegate, and deserialize the property into
 * our desired Object instance
 */
// entry point
fun<T> viewDelegate(default: T?=null):ReadWriteProperty<View,T> =  ViewDelegate(default)

@Suppress("UNCHECKED_CAST")
private class ViewDelegate<T>(private val default:T?=null):ReadWriteProperty<View,T>{
    override fun getValue(thisRef: View, property: KProperty<*>): T {
        // associate a view's tag with a map of objects
        val map = thisRef
            .getOrPutTag<MutableMap<String, Any?>>(R.id.view_delegate_property_map, ::mutableMapOf)
        return (map[property.name] ?: default) as T
    }

    override fun setValue(thisRef: View, property: KProperty<*>, value: T) {
        val map = thisRef
            .getOrPutTag<MutableMap<String, Any?>>(R.id.view_delegate_property_map, ::mutableMapOf)
        map[property.name] = value
    }
}
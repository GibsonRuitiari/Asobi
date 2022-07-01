package com.gibsonruitiari.asobi.data.network.networkextensions

import java.io.Serializable

internal fun <A,B,C>Triple<Iterable<A>,Iterable<B>,Iterable<C>>.iterator():Iterator<Triple<A,B,C>>{
    val param1 = first.iterator()
    val param2= second.iterator()
    val param3= third.iterator()
    return object:Iterator<Triple<A,B,C>>{
        override fun hasNext(): Boolean = param1.hasNext() && param2.hasNext() && param3.hasNext()
        override fun next(): Triple<A, B, C> {
            return Triple(param1.next(),param2.next(),param3.next())
        }
    }
}

internal  fun<A,B,C,D> Container<Iterable<A>, Iterable<B>, Iterable<C>, Iterable<D>>.iterator(): Iterator<Container<A, B, C, D>>{
     val param1 = first.iterator()
    val param2 =second.iterator()
    val param3 = third.iterator()
    val param4 = fourth.iterator()
    return object :Iterator<Container<A, B, C, D>>{
        override fun next(): Container<A, B, C, D> = Container(param1.next(),param2.next(),param3.next(),param4.next())

        override fun hasNext(): Boolean  = param1.hasNext() && param2.hasNext() && param3.hasNext() && param4.hasNext()
    }
}
// adapted from Kotlin's Triple class
data class Container<out A,out B,out C,out D>(val first:A, val second:B, val third:C, val fourth:D):Serializable
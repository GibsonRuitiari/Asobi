package com.gibsonruitiari.asobi.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

open class ViewState
interface State
interface Action
interface Effect

interface Store<S:State,A:Action,E:Effect>{
    fun observeState():StateFlow<S>
    fun observeSideEffect():Flow<E>
    fun onAction(action:A)
}
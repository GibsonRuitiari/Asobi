package com.gibsonruitiari.asobi.domain

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.gibsonruitiari.asobi.data.network.NetworkState
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulates paging data --> basically a list gotten from an api call,
 * the network state,
 * the refresh option and refresh state
 * Internally, [PagingData] uses a flow to represent data, thus there is no need to wrap the paging data in a flow instead,
 * we wrap it in a live data
 */
data class AsobiDataSnapshot<T:Any>(val comicPagingData:LiveData<PagingData<T>>,
val networkState: NetworkState,val refresh:()->Unit,
val refreshState:Flow<NetworkState>,val retry:()->Unit)
package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.domain.interactor.PagedCompletedComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedLatestComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedOngoingComicsObserver
import com.gibsonruitiari.asobi.domain.interactor.PagedPopularComicsObserver

class DiscoverViewModel constructor(private val logger: Logger,
 val pagedCompletedComicsObserver:PagedCompletedComicsObserver,
 val pagedOngoingComicsObserver: PagedOngoingComicsObserver,
 val pagedLatestComicsObserver: PagedLatestComicsObserver,
 val pagedPopularComicsObserver: PagedPopularComicsObserver):ViewModel(){


}
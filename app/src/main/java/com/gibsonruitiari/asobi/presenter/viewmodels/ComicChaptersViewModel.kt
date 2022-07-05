package com.gibsonruitiari.asobi.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.common.CoroutineScopeOwner
import com.gibsonruitiari.asobi.common.Store
import com.gibsonruitiari.asobi.domain.interactor.observers.ComicChaptersObserver
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicChapterAction
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicChapterState
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicChaptersSideEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComicChaptersViewModel constructor(private val comicChaptersUseCase:ComicChaptersObserver):
ViewModel(),CoroutineScopeOwner,Store<ComicChapterState,ComicChapterAction,ComicChaptersSideEffect>{
    override val coroutineScope: CoroutineScope
        get() = viewModelScope
    private val chapterUrl = MutableStateFlow<String?>(null)
    private val state = MutableStateFlow(ComicChapterState.empty)
    private val sideEffect = MutableSharedFlow<ComicChaptersSideEffect>()
    init {
        onAction(ComicChapterAction.LoadComicChapter)
    }
    override fun observeState(): StateFlow<ComicChapterState> =state

    override fun observeSideEffect(): Flow<ComicChaptersSideEffect> = sideEffect

    override fun onAction(action: ComicChapterAction) {
        val oldState = state.value
        when(action){
            is ComicChapterAction.LoadComicChapter->{
                with(state){
                    chapterUrl.value?.let {
                        comicChaptersUseCase.execute(ComicChaptersObserver.ComicPagesParam(it)){
                          onStart {
                              coroutineScope.launch {
                                  emit(oldState.copy(isChapterLoading = false))
                              }
                          }
                          onNext {
                              coroutineScope.launch {
                                  emit(oldState.copy(isChapterLoading = false, comicChapterResult = it))
                              }
                          }
                          onError { err->
                              coroutineScope.launch { emit(oldState.copy(isChapterLoading = false)) }
                              onAction(ComicChapterAction.Error(err.message ?: "Something went wrong please try again"))
                          }
                        }
                    }
                }
            }
            is ComicChapterAction.Error->{
                coroutineScope.launch {
                    sideEffect.emit(ComicChaptersSideEffect.Error(action.message))
                }
            }
        }
    }

}
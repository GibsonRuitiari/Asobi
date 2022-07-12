package com.gibsonruitiari.asobi.ui.comicdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.domain.CoroutineScopeOwner
import com.gibsonruitiari.asobi.utilities.Store
import com.gibsonruitiari.asobi.domain.comicdetails.ComicsDetailsObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComicsDetailsViewModel constructor(private val comicDetailsUseCase: ComicsDetailsObserver):ViewModel(),
    CoroutineScopeOwner,Store<ComicDetailsState,
            ComicDetailsAction, ComicDetailsSideEffect>{
    override val coroutineScope: CoroutineScope
        get() = viewModelScope
    private val comicUrl = MutableStateFlow<String?>(null)
    private val state = MutableStateFlow(ComicDetailsState.Empty)
    private val sideEffect = MutableSharedFlow<ComicDetailsSideEffect>()
    init {

        onAction(ComicDetailsAction.LoadComicDetails)
    }
    override fun observeState(): StateFlow<ComicDetailsState> = state
    fun setComicUrl(url:String){
        comicUrl.value = url
    }
    override fun observeSideEffect(): Flow<ComicDetailsSideEffect> = sideEffect

    override fun onAction(action: ComicDetailsAction) {
       val oldState = state.value
        when(action){
            is ComicDetailsAction.LoadComicDetails->{
                with(state){
                    comicUrl.value?.let {
                        comicDetailsUseCase.execute(ComicsDetailsObserver.ComicDetailsParams(it)){
                            onStart {
                                coroutineScope.launch {
                                    emit(oldState.copy(isLoading = false))
                                }
                            }
                            onNext {
                                coroutineScope.launch {
                                    emit(oldState.copy(isLoading = false, comicsDetailsResult = it))
                                }
                            }
                            onError {
                                coroutineScope.launch { emit(oldState.copy(isLoading = false)) }
                                onAction(ComicDetailsAction.Error(it.message?:"Something went wrong"))
                            }
                        }
                    }
                }
            }
            is ComicDetailsAction.Error->{
                coroutineScope.launch {
                    sideEffect.emit(ComicDetailsSideEffect.Error(action.message))
                }
            }
        }
    }

}
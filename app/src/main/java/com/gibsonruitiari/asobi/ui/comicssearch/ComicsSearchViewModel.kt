package com.gibsonruitiari.asobi.ui.comicssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.domain.CoroutineScopeOwner
import com.gibsonruitiari.asobi.domain.searchcomics.SearchComicsUseCase
import com.gibsonruitiari.asobi.ui.uiModels.UiGenreModel
import com.gibsonruitiari.asobi.ui.uiModels.toUiGenreModel
import com.gibsonruitiari.asobi.utilities.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ComicsSearchViewModel constructor(private val
searchComicsUseCase: SearchComicsUseCase):ViewModel(),CoroutineScopeOwner,Store<SearchComicsState,SearchComicsAction,SearchComicsSideEffect>{
    private val _searchResult = MutableStateFlow(SearchComicsState.empty)
    private val sideEffect = MutableSharedFlow<SearchComicsSideEffect>()
    private val searchQuery = MutableStateFlow("")
    val genres:StateFlow<List<UiGenreModel>> = flowOf(Genres.values().map { it.toUiGenreModel() }).stateIn(viewModelScope,
    started = SharingStarted.WhileSubscribed(500), initialValue = emptyList())
    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override fun observeState(): StateFlow<SearchComicsState> = _searchResult.stateIn(scope=coroutineScope,
    started = SharingStarted.WhileSubscribed(5000),initialValue = SearchComicsState.empty)

    override fun observeSideEffect(): Flow<SearchComicsSideEffect> = sideEffect

    @OptIn(FlowPreview::class)
    override fun onAction(action: SearchComicsAction) {
       val oldState = _searchResult.value
        when(action){
            is SearchComicsAction.ExecuteSearch->{
               coroutineScope.launch {
                   searchQuery.debounce(500)
                       .collectLatest { query->
                           if (query.isNotBlank() && query.isNotEmpty()){
                               val searchParam=SearchComicsUseCase.SearchComicsUseCaseParams(query)
                               searchComicsUseCase.execute(searchParam){
                                   onStart {
                                       coroutineScope.launch { _searchResult.emit(oldState.copy(isLoading = true,noSearchQuery = false)) }
                                   }
                                   onNext {
                                       coroutineScope.launch {
                                           _searchResult.emit(oldState.copy(isLoading = false, searchResults = it,noSearchQuery = false))
                                       }
                                   }
                                   onError {
                                       coroutineScope.launch { _searchResult.emit(oldState.copy(isLoading = false,noSearchQuery = false)) }
                                       onAction(SearchComicsAction.Error(it.message?:"Something went wrong please try again later"))
                                   }
                               }
                           }else{
                               // no search query provided
                               coroutineScope.launch { _searchResult.emit(oldState.copy(isLoading = false,noSearchQuery = true)) }
                           }
                       }
               }
            }
            is SearchComicsAction.Error->{
                coroutineScope.launch {
                    sideEffect.emit(SearchComicsSideEffect.Error(action.message))
                }
            }
        }
    }
    fun setSearchTerm(query:String){
        if (query.isNotBlank() && query.isNotEmpty()){
            searchQuery.value = query
            println("search view model received: ${searchQuery.value}")
            onAction(SearchComicsAction.ExecuteSearch)

        }
    }
    fun clearSearchResult(){
        searchQuery.value =""
        _searchResult.value=SearchComicsState.empty
    }

    override fun onCleared() {
        super.onCleared()
        clearSearchResult()
    }

}
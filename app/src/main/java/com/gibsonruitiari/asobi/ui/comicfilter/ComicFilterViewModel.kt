package com.gibsonruitiari.asobi.ui.comicfilter

import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.ui.uiModels.FilterChip
import com.gibsonruitiari.asobi.ui.uiModels.asChip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

interface ComicFilterViewModel {
    val genresList:StateFlow<List<FilterChip>>
    val selectedFilterChip:StateFlow<FilterChip>
    fun setContentAlpha(float: Float)
    val contentAlpha:StateFlow<Float>
    fun setGenre(selectedGenre: Genres)
    fun resetFilterChoice()
}
class ComicFilterViewModelImpl(private val scope:CoroutineScope):ComicFilterViewModel{

    /* for internal logic  */
    private val _selectedGenre = MutableStateFlow(Genres.DC_COMICS) // by default it is dc_comics
    private val _selectedFilterChip = MutableStateFlow(_selectedGenre.value.asChip(true))
    private val _contentAlpha = MutableStateFlow(1f)

    override val contentAlpha: StateFlow<Float>
        get() = _contentAlpha

    override fun setContentAlpha(float: Float) {
        _contentAlpha.value = float
    }
    override val genresList:StateFlow<List<FilterChip>>
    get() {
        return flowOf(Genres.values().map { it.asChip(it==_selectedGenre.value)}).stateIn(scope = scope,
        initialValue = emptyList(), started = SharingStarted.Eagerly)
    }
    override val selectedFilterChip: StateFlow<FilterChip>
        get() = _selectedFilterChip

    override fun setGenre(selectedGenre:Genres) {
       _selectedGenre.value= selectedGenre
        _selectedFilterChip.value = _selectedGenre.value.asChip(true)
    }
    override fun resetFilterChoice() {
       // reset to default
        _selectedGenre.value = Genres.DC_COMICS
    }
}
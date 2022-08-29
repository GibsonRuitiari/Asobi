package com.gibsonruitiari.asobi.ui.comicsbygenre

import com.gibsonruitiari.asobi.data.datamodels.Genres
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface ComicFilterViewModel {
    fun setGenre(selectedGenre: Genres)
    val currentGenreChoice:StateFlow<Genres?>
    fun resetGenreChoice()
}
class ComicFilterViewModelDelegate:ComicFilterViewModel{
    private val _selectedGenre = MutableStateFlow<Genres?>(null) // by defaukt
    override val currentGenreChoice: StateFlow<Genres?>
        get() = _selectedGenre

    override fun resetGenreChoice() {
       _selectedGenre.value = Genres.DC_COMICS
    }
    override fun setGenre(selectedGenre: Genres) {
        _selectedGenre.update { selectedGenre }
        println("genre ${_selectedGenre.value}")
    }
}
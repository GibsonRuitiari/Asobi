package com.gibsonruitiari.asobi.ui.uiModels

import android.graphics.Color
import com.gibsonruitiari.asobi.data.datamodels.Genres

data class FilterChip(val genres: Genres,val isSelected:Boolean,
val color:Int= Color.parseColor("#4768fd"),val selectedTextColor:Int=Color.WHITE,
val text:String="")

fun Genres.asChip(isGenreSelected:Boolean):FilterChip = when(this){
    Genres.MARVEL -> FilterChip(isSelected = isGenreSelected,
    genres = this, text = genreName)
    Genres.DC_COMICS -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ACTION -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ADVENTURE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ANTHOLOGY ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ANTHROPOMORPHIC -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.BIOGRAPHY ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.CHILDREN ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.COMEDY -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.CYBORGS -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.DARK_HORSE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.DEMONS -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.DRAMA -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.FANTASY -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.FAMILY -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.FIGHTING -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.GORE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.GRAPHIC_NOVELS -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.HISTORICAL ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.LEADING_LADIES -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.LITERATURE ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MAGIC ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MANGA -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MARTIAL_ARTS -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MECHA ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MATURE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MILITARY -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MOVIE_CINEMATIC -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MYSTERY ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.MYTHOLOGY ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.PERSONAL -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.POLITICAL -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.PSYCHOLOGICAL -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.POST_APOCALYPTIC ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.PULP -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SCI_FI -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ROMANCE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ROBOTS -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SPY ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SPORTS ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SUPERHERO -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SUPERNATURAL -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SUSPENSE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SCIENCE_FICTION -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.SLICE_OF_LIFE -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.THRILLER -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.TRAGEDY -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.VAMPIRES -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.VERTIGO ->FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.VIDEO_GAMES -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.WAR -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.WESTERN -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
    Genres.ZOMBIES -> FilterChip(isSelected = isGenreSelected,
        genres = this, text = genreName)
}


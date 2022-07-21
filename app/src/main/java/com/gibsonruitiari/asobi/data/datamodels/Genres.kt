package com.gibsonruitiari.asobi.data.datamodels

enum class Genres (val genreName:String, val emoji:String?=null) {
   MARVEL("marvel", "\uD83E\uDD2F"),
    DC_COMICS("dc-comics", "\uD83D\uDE0C"),
    ACTION("action", "\uD83D\uDD2B"),
    ADVENTURE("adventure", "\uD83E\uDDD7"),
    ANTHOLOGY("anthology", "\uD83D\uDC63"),
    ANTHROPOMORPHIC("anthropomorphic","\uD83E\uDD8D"),
    BIOGRAPHY("biography", "\uD83E\uDDD3"),
    CHILDREN("children", "\uD83E\uDDD2"),
    COMEDY("comedy", "\uD83D\uDE02"),
    HORROR("horror",null),
    DRAMA("drama","\uD83E\uDD21"),
    FANTASY("fantasy", "\uD83E\uDDDC"),
    FAMILY("family", "\uD83D\uDC6A"),
    FIGHTING("fighting", "\uD83E\uDD3C"),
    GRAPHIC_NOVELS("graphic-novels","\uD83D\uDCD3"),
    HISTORICAL("historical", "\uD83D\uDDFF"),
    LEADING_LADIES("leading-ladies", "\uD83D\uDC6F"),
    LITERATURE("literature", "\uD83D\uDCDA"),
    MAGIC("magic", "\uD83E\uDDDA"),
    MANGA("manga", "\uD83C\uDF8E"),
    MARTIAL_ARTS("martial-arts", "\uD83E\uDD4B"),
    MATURE("mature","\uD83D\uDC59"),
    MILITARY("military", "\uD83C\uDF96️"),
    MYSTERY("mystery", "\uD83D\uDD2E"),
    MYTHOLOGY("mythology", "\uD83C\uDF83"),
    POLITICAL("political", "\uD83D\uDDFD"),
    POST_APOCALYPTIC("post-apocalyptic","\uD83E\uDDDF"),
    PULP("pulp",""),
    SCI_FI("sci-fi","\uD83D\uDC7D"),
    ROMANCE("romance", "\uD83D\uDC91"),
    ROBOTS("robots","\uD83E\uDD16"),
    SPY("spy", "\uD83E\uDDD0"),
 CRIME("crime",null),
    SUPERHERO("superhero", "\uD83E\uDDB8"),
    SUPERNATURAL("supernatural", "\uD83E\uDDDE"),
    SUSPENSE("suspense","\uD83D\uDE28"),
    SCIENCE_FICTION("science-fiction", "\uD83D\uDE80"),
    SLICE_OF_LIFE("slice-of-life", "\uD83E\uDD17"),
    THRILLER("thriller", "\uD83D\uDE35"),
    VAMPIRES("vampires", "\uD83C\uDFAE"),
    VIDEO_GAMES("video-games", "\uD83D\uDD79️"),
    WAR("war", "\uD83D\uDD25"),
    WESTERN("western", "\uD83C\uDFA1"),
    ZOMBIES("zombies", "\uD83E\uDDDF");
 companion object{
      private val values by lazy{ values() }
     fun fromGenreValue(value:String)= values.firstOrNull { it.genreName==value }
 }


}
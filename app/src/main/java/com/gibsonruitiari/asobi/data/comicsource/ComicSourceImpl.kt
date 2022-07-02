package com.gibsonruitiari.asobi.data.comicsource

import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.datamodels.SMangaChapter
import com.gibsonruitiari.asobi.data.datamodels.SMangaIssue
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.data.datamodels.SMangaPage
import com.gibsonruitiari.asobi.data.network.networkextensions.Container
import com.gibsonruitiari.asobi.data.network.networkextensions.iterator
import org.jsoup.nodes.Document
import java.util.*

internal class ComicSourceImpl:ParsedHttpSource() {
    override fun mangaDetailsParse(document: Document): SMangaInfo {
        val mangaInfo = SMangaInfo.create()
        document.select("div.anime-details").run {
            val animeImage = this.select("div.anime-image ").map { it.select("img").attr("src")}.first()
            val status = this.select("li.status").map { it.text() }.first()
            val genres=this.select("ul.anime-genres li a[href^=https]").map { it.text() }
            val alternateName = this.select("table.full-table tbody tr:eq(1)").joinToString { it.text() }
                .replace("Alternate Name: ","")
            val yearOfRelease = this.select("table.full-table tbody tr:eq(2)").joinToString { it.text() }
                .replace("Year of Release: ","")
            val author = this.select("table.full-table tbody tr:eq(3)").joinToString { it.text() }.replace("Author: ","")
            val views = this.select("table.full-table tbody tr:eq(4)").joinToString { it.text() }.replace("Views: ","")
            mangaInfo.comicAuthor = author
            mangaInfo.comicViews = views.toDouble()
            mangaInfo.yearOfRelease = yearOfRelease
            mangaInfo.comicAlternateName = alternateName
            mangaInfo.genres = genres
            mangaInfo.comicStatus = status
            mangaInfo.comicImagePosterLink = animeImage
        }
        mangaInfo.issues = document.select("ul.basic-list li").run {
            val issuesReleasedDate= this.select("span").map { it.text() }
            val issuesLink = this.select("a[href]").map { it.attr("href") }
            val issuesNames= this.select("a[href]").map { it.text() }
            val issuesList = mutableListOf<SMangaIssue>()
            Triple(issuesReleasedDate, issuesLink,issuesNames).iterator().forEach {
                val mangaIssues = SMangaIssue.create()
                mangaIssues.issueLink = it.second
                mangaIssues.issueName= it.third
                mangaIssues.issueReleaseDate = it.first
                issuesList.add(mangaIssues)
            }
            return@run issuesList.toList()
        }

        mangaInfo.similarManga =document.select("div.image-genre-list div.ig-box").run {
            val names = this.select("a.igb-name").map { it.text() }
            val comicLinks=this.select("> a[href].igb-image").map {it.attr("href")}
            val imageLinks =this.select("a[href].igb-image ").map {it.select("img").attr("src")}
            val similarMangasList = mutableListOf<SManga>()
            Triple(names,comicLinks,imageLinks).iterator().forEach {
                val manga = SManga.create()
                manga.comicName = it.first
                manga.comicLink = it.second
                manga.comicThumbnailLink = it.third
                similarMangasList.add(manga)
            }
            return@run  similarMangasList
        }
        mangaInfo.comicDescription = document.select("div.detail-desc-content p").map { it.text() }.first()
        return mangaInfo
    }

    override fun mangaPopularAndNewNextPageSelector(): String? = null

    override fun mangaPopularAndNewTitleSelector(): String = "div.egb-right a[href]"

    override fun mangaPopularAndNewThumbnailSelector(): String = "a[href].eg-image "

    override fun mangaPopularAndNewMangaLinkSelector(): String ="> a[href].eg-image"

    override fun latestEpisodeLinkLinkSelector(): String ="a.egb-episode[href]"

    override fun popularAndNewMangaFromDocument(document: Document): List<SManga> {
        val mangas = mutableListOf<SManga>()
        document.select("div.eg-box").run {
            val names = this.select(mangaPopularAndNewTitleSelector()).map {
                it.text()
            }
            val comicLinks = this.select(mangaPopularAndNewMangaLinkSelector()).map {
                it.attr("href")
            }
            val imageLinks = this.select(mangaPopularAndNewThumbnailSelector()).map {
                it.select("img").attr("src")
            }
            val latestEpisodeLink = this.select(latestEpisodeLinkLinkSelector()).map {
                it.attr("href")
            }

            when{
                latestEpisodeLink.isEmpty()->{
                    // we are dealing with popular manga  not new mangas
                    Triple(names, comicLinks, imageLinks).iterator().forEach {
                        val manga = SManga.create()
                        manga.comicName = it.first
                        manga.comicLink = it.second
                        manga.comicThumbnailLink = it.third
                        manga.latestIssue = null// null here
                        mangas.add(manga)
                    }
                }
                else->{
                    Container(names,comicLinks,imageLinks,latestEpisodeLink).iterator().forEach {
                        val manga = SManga.create()
                        manga.comicName = it.first
                        manga.comicLink = it.second
                        manga.comicThumbnailLink= it.third
                        manga.latestIssue = it.fourth.cleanIssueLink()
                        mangas.add(manga)
                    }
                }
            }
        }
        return mangas
    }

    override fun mangaByGenreAndOngoingNextPageSelector(): String?  = null


    override fun mangaByGenreAndOngoingTitleSelector(): String = "a.igb-name"

    override fun mangaByGenreAndOngoingThumbnailSelector(): String = "a[href].igb-image"
    override fun mangaByGenreAndOngoingMangaLinkSelector(): String = "> a[href].igb-image"

    override fun mangaByGenreAndOngoingFromDocument(document: Document): List<SManga> {
        val mangas = mutableListOf<SManga>()
        document.select("div.ig-box ").run {
            val mangaNames = this.select(mangaByGenreAndOngoingTitleSelector()).map {
                it.text()
            }
            val mangaLink = this.select(mangaByGenreAndOngoingMangaLinkSelector()).map {
                it.attr("href")
            }
            val mangaImageLink = this.select(mangaByGenreAndOngoingThumbnailSelector()).map {
                it.select("img").attr("src")
            }
            Triple(mangaNames, mangaLink,mangaImageLink).iterator().forEach {

                val manga = SManga.create()
                manga.comicLink = it.second
                manga.comicName = it.first
                manga.comicThumbnailLink = it.third
                manga.latestIssue = null
                mangas.add(manga)
            }
        }
        return mangas
    }



    override fun mangaIssuesFromDocument(document: Document): SMangaChapter {
        return document.select("div.chapter-container img[src^=https]").run {
            val pages = this.map { it.attr("src") }
            val pageDetails = this.map { it.attr("alt") }
            val chapter = SMangaChapter.create()
            val mangaPagesList= pages.zip(pageDetails).map {
                val mangaPage = SMangaPage.create()
                val pageLink = it.first
                val pageDetail = it.second
                mangaPage.pageDetail = pageDetail
                mangaPage.pageThumbnail= pageLink
                return@map mangaPage
            }
            chapter.pages = mangaPagesList
            return@run chapter
        }
    }

    override fun mangaCompletedNextPageSelector(): String? = null



    override fun mangaCompletedThumbnailSelector(): String = "a[href].dlb-image"

    override fun mangaCompletedMangaLinkSelector(): String ="a[href].dlb-image"

    override fun mangaCompletedFromDocument(document: Document): List<SManga> {
        val completedMangaList = mutableListOf<SManga>()
        document.select("div.detailed-list div.dl-box").run {
            val imageLinks = this.select(mangaCompletedThumbnailSelector()).map {
                it.select("img").attr("src")
            }
            val comicLinks = this.select(mangaCompletedMangaLinkSelector()).map {
                it.attr("href")
            }
            val completedMangaNames = comicLinks.map {
                it.split("comic").last().replace("/","").replace("-"," ")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()}
            }

            Triple(completedMangaNames, comicLinks, imageLinks).iterator().forEach {
                val sManga = SManga.create() // create a new instance on iteration
                sManga.comicLink = it.second
                sManga.comicName= it.first
                sManga.comicThumbnailLink = it.third
                sManga.latestIssue = null
                completedMangaList.add(sManga)
            }


        }
        return completedMangaList

    }

    private fun String.cleanIssueLink():String =  "${this}/full"
}
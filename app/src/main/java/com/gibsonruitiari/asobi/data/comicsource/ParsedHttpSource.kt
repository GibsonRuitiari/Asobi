package com.gibsonruitiari.asobi.data.comicsource

import com.gibsonruitiari.asobi.data.datamodels.MangaPage
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.datamodels.SMangaChapter
import com.gibsonruitiari.asobi.data.datamodels.SMangaInfo
import com.gibsonruitiari.asobi.data.network.networkextensions.asJsoup
import okhttp3.Response
import org.jsoup.nodes.Document

internal abstract class ParsedHttpSource:HttpSource() {
    override fun parseCompletedComicsParse(response: Response): MangaPage {
        return response.asJsoup().run {
            val mangas = mangaCompletedFromDocument(this)
            return@run MangaPage(mangas,true)
        }
    }
    override fun parseOnGoingComicsAndBasedGenreParse(response: Response): MangaPage {
        return response.asJsoup().run {
            val mangas = mangaByGenreAndOngoingFromDocument(this)
            return@run MangaPage(mangas,true)
        }
    }
    override fun parsePopularAndNewComicsParse(response: Response): MangaPage {
        return response.asJsoup().run {
            val mangas = popularAndNewMangaFromDocument(this)
            return@run MangaPage(mangas,true)
        }
    }
    override fun parseSearchComicsParse(response: Response): List<SManga> {
        return response.asJsoup().run { return@run mangaCompletedFromDocument(this) }
    }
    override fun mangaDetailsParse(response: Response): SMangaInfo {
        return mangaDetailsParse(response.asJsoup())
    }
    override fun parseMangaIssue(response: Response): SMangaChapter {
        return mangaIssuesFromDocument(response.asJsoup())
    }
    /**
     * Returns the details of the manga from the given [document].
     *
     * @param document the parsed document.
     */
    protected abstract fun mangaDetailsParse(document: Document): SMangaInfo
    /**
     * Needed selectors to get the popular and new comics
     */
    protected abstract fun mangaPopularAndNewNextPageSelector():String?
    protected abstract fun mangaPopularAndNewTitleSelector():String
    protected abstract fun mangaPopularAndNewThumbnailSelector():String
    protected abstract fun mangaPopularAndNewMangaLinkSelector():String
    protected abstract fun latestEpisodeLinkLinkSelector():String?
    protected abstract fun popularAndNewMangaFromDocument(document: Document):List<SManga>

    /**
     * Needed selectors to get the comics per genre and ongoing comics
     */
    protected abstract fun mangaByGenreAndOngoingNextPageSelector():String?
    protected abstract fun mangaByGenreAndOngoingTitleSelector():String
    protected abstract fun mangaByGenreAndOngoingThumbnailSelector():String
    protected abstract fun mangaByGenreAndOngoingMangaLinkSelector():String
    protected abstract fun mangaByGenreAndOngoingFromDocument(document: Document):List<SManga>
    /**
     * Needed selectors to get the issues
     */

    protected abstract fun mangaIssuesFromDocument(document: Document): SMangaChapter

    /**
     * Needed selectors for completed comics
     */
    protected abstract fun mangaCompletedNextPageSelector():String?
    protected abstract fun mangaCompletedThumbnailSelector():String
    protected abstract fun mangaCompletedMangaLinkSelector():String
    protected abstract fun mangaCompletedFromDocument(document: Document):List<SManga>

}
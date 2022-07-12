package com.gibsonruitiari.asobi.domain.comicchapters

import com.gibsonruitiari.asobi.utilities.utils.sMangaChapterToViewMangaChapter
import com.gibsonruitiari.asobi.utilities.utils.toNetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.shared.comicsbychapter.ComicsChapterRepo
import com.gibsonruitiari.asobi.domain.FlowUseCase
import com.gibsonruitiari.asobi.ui.comichapters.ComicChapterResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ComicChaptersObserver constructor(private val comicsChapterRepo: ComicsChapterRepo):
    FlowUseCase<ComicChaptersObserver.ComicPagesParam, ComicChapterResult>() {
    override fun run(params: ComicPagesParam): Flow<ComicChapterResult> = comicsChapterRepo.getComicsChapter(params.comicLink).map {
            sMangaChapterToViewMangaChapter(it)
        }.toNetworkResource().map {
            it.data?.comicPages
            ComicChapterResult(
                ComicChapterResult.ComicChapterPages(it.status==Status.LOADING,
            errorMessage = it.throwable?.message, comicPages = it.data?.comicPages ?: emptyList(), totalNumberOfPages = it.data?.totalPages))
        }

    data class ComicPagesParam(val comicLink:String)

}
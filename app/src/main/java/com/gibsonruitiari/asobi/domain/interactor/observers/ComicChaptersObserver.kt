package com.gibsonruitiari.asobi.domain.interactor.observers

import com.gibsonruitiari.asobi.common.sMangaChapterToViewMangaChapter
import com.gibsonruitiari.asobi.common.utils.toNetworkResource
import com.gibsonruitiari.asobi.data.comicPages
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.domain.interactor.FlowUseCase
import com.gibsonruitiari.asobi.presenter.uicontracts.ComicChapterResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ComicChaptersObserver:FlowUseCase<ComicChaptersObserver.ComicPagesParam,ComicChapterResult>() {
    override fun run(params: ComicPagesParam): Flow<ComicChapterResult> = comicPages(params.comicLink).map {
            sMangaChapterToViewMangaChapter(it)
        }.toNetworkResource().map {
            it.data?.comicPages
            ComicChapterResult(ComicChapterResult.ComicChapterPages(it.status==Status.LOADING,
            errorMessage = it.throwable?.message, comicPages = it.data?.comicPages ?: emptyList(), totalNumberOfPages = it.data?.totalPages))
        }

    data class ComicPagesParam(val comicLink:String)

}
package com.gibsonruitiari.asobi.domain

import com.gibsonruitiari.asobi.data.datamodels.Genres
import com.gibsonruitiari.asobi.utilities.toNetworkResource
import com.gibsonruitiari.asobi.data.datamodels.SManga
import com.gibsonruitiari.asobi.data.network.NetworkResource
import com.gibsonruitiari.asobi.data.network.Status
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreRepo
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverComicsResult
import com.gibsonruitiari.asobi.utilities.extensions.parseThrowableErrorMessageIntoUsefulMessage
import com.gibsonruitiari.asobi.utilities.sMangaToViewComicMapper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiscoverComicsUseCase constructor(private val latestComicsRepo: LatestComicsRepo,
                                        private val ongoingComicsRepo: OngoingComicsRepo,
                                        private val popularComicsRepo: com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo,
                                        private val completedComicsRepo: CompletedComicsRepo,
                                        private val genreComicsRepo: ComicsByGenreRepo
): FlowUseCase<DiscoverComicsUseCase.DiscoverComicsParams, DiscoverComicsResult>() {
    override fun run(params: DiscoverComicsParams): Flow<DiscoverComicsResult> {

        return  combine( completedComicsRepo
                .getCompletedComics(params.page)
                .toNetworkResource()
                .toComicsResultData(),
                latestComicsRepo.getLatestComics(params.page)
                    .toNetworkResource()
                    .toComicsResultData(),
                popularComicsRepo.getPopularComics(params.page)
                    .toNetworkResource()
                    .toComicsResultData(),
                ongoingComicsRepo.getOngoingComics(params.page)
                    .toNetworkResource()
                    .toComicsResultData(),
                genreComicsRepo.getComicsByGenre(params.page,Genres.MARVEL)
                    .toNetworkResource()
                    .toComicsResultData(),
                genreComicsRepo.getComicsByGenre(params.page,Genres.DC_COMICS)
                    .toNetworkResource()
                    .toComicsResultData()){
                val latest = it[0]
                val completed = it[1]
                val popular=it[2]
                val ongoing=it[3]
                val marvelComics=it[4]
                val dcComics =it[5]
                            DiscoverComicsResult(
                latestComics = latest,
                completedComics = completed,
                popularComics = popular,
                ongoingComics = ongoing,
                marvelComics = marvelComics,
                dcComics = dcComics)
            }

    }

    data class DiscoverComicsParams(val page:Int)
    private fun Flow<NetworkResource<List<SManga>>>.toComicsResultData() =map{
        DiscoverComicsResult.DiscoverComicsData(isLoading = it.status == Status.LOADING,
        comicsData =it.data?.map { sMangaToViewComicMapper(it)
        }?.take(14) ?: emptyList(),
            errorMessage = it.throwable?.parseThrowableErrorMessageIntoUsefulMessage())
    }
    //

}
package com.gibsonruitiari.asobi.di

import com.gibsonruitiari.asobi.data.shared.comicdetails.ComicsDetailsRepo
import com.gibsonruitiari.asobi.data.shared.comicdetails.ComicsDetailsRepoImpl
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreDataSource
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreRepo
import com.gibsonruitiari.asobi.data.shared.comicsbygenre.ComicsByGenreRepoImpl
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsDataSource
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepo
import com.gibsonruitiari.asobi.data.shared.completedcomics.CompletedComicsRepoImpl
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsDataSource
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepo
import com.gibsonruitiari.asobi.data.shared.latestcomics.LatestComicsRepoImpl
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsDataSource
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepo
import com.gibsonruitiari.asobi.data.shared.ongoingcomics.OngoingComicsRepoImpl
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsDataSource
import com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepoImpl
import com.gibsonruitiari.asobi.data.shared.searchcomics.SearchComicsDelegate
import com.gibsonruitiari.asobi.data.shared.searchcomics.SearchComicsRepo
import com.gibsonruitiari.asobi.domain.DiscoverComicsUseCase
import com.gibsonruitiari.asobi.domain.bygenre.PagedComicsByGenreObserver
import com.gibsonruitiari.asobi.domain.comicchapters.ComicChaptersObserver
import com.gibsonruitiari.asobi.domain.comicdetails.ComicsDetailsObserver
import com.gibsonruitiari.asobi.domain.completedcomics.PagedCompletedComicsObserver
import com.gibsonruitiari.asobi.domain.latestcomics.PagedLatestComicsObserver
import com.gibsonruitiari.asobi.domain.ongoingcomics.PagedOngoingComicsObserver
import com.gibsonruitiari.asobi.domain.popularcomics.PagedPopularComicsObserver
import com.gibsonruitiari.asobi.domain.searchcomics.SearchComicsUseCase
import com.gibsonruitiari.asobi.ui.MainActivityViewModel
import com.gibsonruitiari.asobi.ui.comicdetails.ComicsDetailsViewModel
import com.gibsonruitiari.asobi.ui.comichapters.ComicChaptersViewModel
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModel
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicFilterViewModelDelegate
import com.gibsonruitiari.asobi.ui.comicsbygenre.ComicsByGenreViewModel
import com.gibsonruitiari.asobi.ui.comicssearch.ComicsSearchViewModel
import com.gibsonruitiari.asobi.ui.completedcomics.CompletedComicsViewModel
import com.gibsonruitiari.asobi.ui.discovercomics.DiscoverViewModel
import com.gibsonruitiari.asobi.ui.latestcomics.LatestComicsViewModel
import com.gibsonruitiari.asobi.ui.ongoingcomics.OngoingComicsViewModel
import com.gibsonruitiari.asobi.ui.popularcomics.PopularComicsViewModel
import com.gibsonruitiari.asobi.utilities.logging.AsobiLogger
import com.gibsonruitiari.asobi.utilities.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val asobiLoggerModule = module {
    // provide only one instance across the application
    single<Logger> { AsobiLogger() }
}
val observersModule = module {
    factory { PagedCompletedComicsObserver(get(), get()) }
    factory { PagedComicsByGenreObserver(get(), get()) }
    factory { PagedLatestComicsObserver(get(), get()) }
    factory { PagedOngoingComicsObserver(get(), get()) }
    factory { PagedPopularComicsObserver(get(), get()) }
    factory { ComicChaptersObserver(get()) }
    factory { ComicsDetailsObserver(get()) }
    factory { SearchComicsUseCase(get(),get()) }
    factory { DiscoverComicsUseCase(get(),get(),get(),get(), get()) }
}

val comicsDataSourcesModule = module {
    factory { ComicsByGenreDataSource(get(), get()) }
    factory { CompletedComicsDataSource(get(), get()) }
    factory { LatestComicsDataSource(get(), get()) }
    factory { PopularComicsDataSource(get(), get()) }
    factory { OngoingComicsDataSource(get(), get()) }
}
val scopeModule = module {
    single(named("applicationScope")) {
        CoroutineScope(SupervisorJob()+Dispatchers.Default)
    }
}
val viewModelsModule= module {
    viewModel { ComicChaptersViewModel(get()) } // get() loads the dependencies eagerly as opposed to lazy loading -:(
    viewModel { ComicsByGenreViewModel(get(),get(named("filtersViewModel"))) }
    viewModel { ComicsDetailsViewModel(get()) }
    viewModel { CompletedComicsViewModel(get()) }
    viewModel{ DiscoverViewModel(get()) }
    viewModel { LatestComicsViewModel(get()) }
    viewModel { OngoingComicsViewModel(get()) }
    viewModel { PopularComicsViewModel(get()) }
    viewModel { MainActivityViewModel() }
    viewModel { ComicsSearchViewModel(get()) }
    factory<ComicFilterViewModel>(named("filtersViewModel")) { ComicFilterViewModelDelegate() }

}

val comicsRepositoryModule= module{
    single<LatestComicsRepo> { LatestComicsRepoImpl() }
    single<OngoingComicsRepo> { OngoingComicsRepoImpl() }
    single<CompletedComicsRepo> { CompletedComicsRepoImpl() }
    single<ComicsDetailsRepo> { ComicsDetailsRepoImpl() }
    single<com.gibsonruitiari.asobi.data.shared.popularcomics.PopularComicsRepo> { PopularComicsRepoImpl() }
    single<ComicsByGenreRepo> { ComicsByGenreRepoImpl() }
    single<SearchComicsRepo> { SearchComicsDelegate() }
}
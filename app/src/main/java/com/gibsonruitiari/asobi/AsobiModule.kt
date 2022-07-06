package com.gibsonruitiari.asobi

import com.gibsonruitiari.asobi.common.logging.AsobiLogger
import com.gibsonruitiari.asobi.common.logging.Logger
import com.gibsonruitiari.asobi.data.repositories.*
import com.gibsonruitiari.asobi.domain.pagingdatasource.*
import com.gibsonruitiari.asobi.domain.repositories.*
import com.gibsonruitiari.asobi.presenter.viewmodels.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val asobiLoggerModule = module {
    // provide only one instance across the application
    single<Logger> { AsobiLogger() }
}
val comicsDataSourcesModule = module {
    factory { ComicsByGenreDataSource(get()) }
    factory { CompletedComicsDataSource(get()) }
    factory { LatestComicsDataSource(get()) }
    factory { PopularComicsDataSource(get()) }
    factory { OngoingComicsDataSource(get()) }
}
val viewModelsModule= module {
    viewModel { ComicChaptersViewModel(get()) } // get() loads the dependencies eargerly as opposed to lazy loading -:(
    viewModel { ComicsByGenreViewModel(get()) }
    viewModel { ComicsDetailsViewModel(get()) }
    viewModel { CompletedComicsViewModel(get()) }
    viewModel{DiscoverViewModel(get())}
    viewModel { LatestComicsViewModel(get()) }
    viewModel { OngoingComicsViewModel(get()) }
    viewModel { PopularComicsViewModel(get()) }
}

val comicsRepositoryModule= module{
    single<LatestComicsRepo> { LatestComicsRepoImpl() }
    single<OngoingComicsRepo> { OngoingComicsRepoImpl() }
    single<CompletedComicsRepo> { CompletedComicsRepoImpl() }
    single<ComicsDetailsRepo> { ComicsDetailsRepoImpl() }
    single<PopularComicsRepo> { PopularComicsRepoImpl() }
    single<ComicsByGenreRepo> { ComicsByGenreRepoImpl() }
}
package com.gibsonruitiari.asobi

import androidx.multidex.MultiDexApplication
import com.gibsonruitiari.asobi.common.logging.AsobiDebugTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class AsobiApplication :MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(AsobiDebugTree())
        }
        // install the modules
        startKoin {
            androidLogger()
            androidContext(this@AsobiApplication)
            modules(listOf(asobiLoggerModule,
            viewModelsModule, observersModule, comicsRepositoryModule, comicsDataSourcesModule))
        }
    }
}
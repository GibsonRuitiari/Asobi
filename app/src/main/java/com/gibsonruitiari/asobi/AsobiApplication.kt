package com.gibsonruitiari.asobi

import android.app.Application
import com.gibsonruitiari.asobi.utilities.logging.AsobiDebugTree
import com.gibsonruitiari.asobi.di.*
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class AsobiApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG){
            Timber.plant(AsobiDebugTree())
        }
        // install the modules
        startKoin {
            androidLogger()
            androidContext(this@AsobiApplication)
            modules(listOf(asobiLoggerModule,viewModelsModule, observersModule, comicsRepositoryModule, comicsDataSourcesModule,scopeModule))
        }
    }
   // less than 1gb ram memory
    override fun onLowMemory() {
        super.onLowMemory()
        GlideApp.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlideApp.get(this).trimMemory(level)
    }
}
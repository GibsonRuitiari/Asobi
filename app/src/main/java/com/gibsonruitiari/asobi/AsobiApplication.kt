package com.gibsonruitiari.asobi

import androidx.multidex.MultiDexApplication
import com.gibsonruitiari.asobi.common.logging.AsobiDebugTree
import timber.log.Timber

class AsobiApplication :MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(AsobiDebugTree())
        }
    }
}
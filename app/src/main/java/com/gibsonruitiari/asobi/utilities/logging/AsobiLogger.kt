package com.gibsonruitiari.asobi.utilities.logging

import timber.log.Timber

class AsobiLogger:Logger {
    override fun setUp(isInDebugMode: Boolean) {
        if (isInDebugMode) Timber.plant(AsobiDebugTree())
    }
    override fun v(throwable: Throwable, message: String, vararg args: Any?) {
        Timber.v(throwable,message, args)
    }

    override fun v(message: String, vararg args: Any?) {
       Timber.v(message, args)
    }

    override fun v(throwable: Throwable) {
       Timber.v(throwable)
    }

    override fun i(message: String, vararg args: Any?) {
        Timber.i(message, args)
    }

    override fun i(throwable: Throwable, message: String, vararg args: Any?) {
        Timber.i(throwable,message, args)
    }

    override fun i(throwable: Throwable) {
        Timber.i(throwable)
    }

    override fun d(message: String, vararg args: Any?) {
       Timber.d(message, args)
    }

    override fun d(throwable: Throwable, message: String, vararg args: Any?) {
       Timber.d(throwable,message, args)
    }

    override fun d(throwable: Throwable) {
       Timber.d(throwable)
    }

    override fun e(message: String, vararg args: Any?) {
      Timber.e(message, args)
    }

    override fun e(throwable: Throwable) {
       Timber.e(throwable)
    }

    override fun e(throwable: Throwable, message: String, vararg args: Any?) {
       Timber.e(throwable,message,args)
    }

}
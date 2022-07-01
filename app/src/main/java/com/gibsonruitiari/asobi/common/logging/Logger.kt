package com.gibsonruitiari.asobi.common.logging


/**
 * Logger entry point to be used throughout the application
 */
interface Logger {
    fun setUp(isInDebugMode:Boolean)
    /* log verbose */
    fun v(throwable: Throwable,message: String,vararg args:Any?)
    fun v(message:String, vararg args:Any?)
    fun v(throwable: Throwable)

    /* log information */
    fun i(message: String,vararg args:Any?)
    fun i(throwable: Throwable,message: String,vararg args:Any?)
    fun i(throwable: Throwable)

    /* log debug */
    fun d(message: String,vararg args:Any?)
    fun d(throwable: Throwable,message: String,vararg args:Any?)
    fun d(throwable: Throwable)

    /* log error */
    fun e(message: String,vararg args:Any?)
    fun e(throwable: Throwable)
    fun e(throwable: Throwable,message: String,vararg args:Any?)

}
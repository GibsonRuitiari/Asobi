package com.gibsonruitiari.asobi.common.logging

import android.os.Build
import timber.log.Timber
import java.util.regex.Pattern

class AsobiDebugTree :Timber.DebugTree(){
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, createClassTag(), message, t)
    }
    private fun createClassTag():String{
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= CALL_STACK_INDEX){
            // pro-gaurd does not preserve method names and line numbers in stack trace
            // see https://stackoverflow.com/questions/53044902/throwable-getstacktrace0-getlinenumber-obfuscated-by-proguard
            throw IllegalStateException("Synthetic stacktrace did not have enough elements are you using Progaurd?")
        }
        var tag = stackTrace[CALL_STACK_INDEX].className // last item in the stack trace
        val m = tagPattern.matcher(tag)
        if (m.find()) tag = m.replaceAll("")
        tag = tag.substring(tag.lastIndexOf('.')+1)
        return when {
            Build.VERSION.SDK_INT >= 24 || tag.length <= MAX_TAG_LENGTH -> tag
            else -> tag.substring(0, MAX_TAG_LENGTH)
        }
    }

    companion object{
        private const val MAX_TAG_LENGTH =23
        /*the index of the bottom stack frame is usually more than 6 so set 7 to be the offset */
        private const val CALL_STACK_INDEX=7
        private  val tagPattern by lazy {
            // strip anonymous class suffixes eg Foo$ to Foo
            Pattern.compile("(\\$\\d+)+$")
        }
    }
}
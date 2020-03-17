package com.skoumal.teagger.timber

import com.skoumal.teagger.StreamLogger
import timber.log.Timber

class TeaggerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // we do not require throwable to be logged since Timber consumes the stacktrace and
        // concatenates it with the existing message
        StreamLogger.instance.log(priority, tag ?: this::class.java.simpleName, message)
    }
}
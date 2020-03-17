package com.skoumal.teagger.timber

import com.skoumal.teagger.Teagger
import timber.log.Timber

class TeaggerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // we do not require throwable to be logged since Timber consumes the stacktrace and
        // concatenates it with the existing message
        Teagger.instance.log(priority, tag ?: this::class.java.simpleName, message)
    }
}
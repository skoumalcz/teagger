package com.skoumal.teagger.crash

import com.skoumal.teagger.StreamLogger

internal class StreamCrashHandler(
    private val logger: StreamLogger
) {

    init {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // todo dump the thread group
            handleCrash(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * First attempts to log crash using the logger's [OutputStreamProvider].
     * If this action throws an exception, attempts to log into a separate file. These logs can
     * be logged into the correct stream another time by calling [pushToLog]. Finally, this method
     * throws the error again so that the application stops.
     * @param throwable the error causing the crash
     */
    private fun handleCrash(throwable: Throwable) {
        logger.log(throwable)
    }

}

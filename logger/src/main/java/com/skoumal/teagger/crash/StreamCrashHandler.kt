package com.skoumal.teagger.crash

import android.content.Context
import com.skoumal.teagger.Constants
import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.setFile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

internal class StreamCrashHandler(
    private val logger: StreamLogger
) {

    @Deprecated("Class needs to be provided with a logger. Creating it by itself defies the contract that logger makes with the crash handler")
    constructor(logger: StreamLogger, context: Context) : this(logger) {
        try {
            val dir = File(
                context.filesDir,
                Constants.FILES_DIR
            ).apply { mkdirs() }
            val file = File(dir, Constants.CRASH_LOG_FILE).apply { createNewFile() }
            logger.setFile(file)
        } catch (t: Throwable) {
            if (t is IOException || t is SecurityException) {
                t.printStackTrace()
            } else {
                throw t
            }
        }
    }

    init {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // todo dump the thread group
            handleCrash(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Call this method in your Application class in order to log any crashes that failed to log
     * to the desired stream.
     */
    @Deprecated("This should not be a responsibility of the crash handler.")
    fun pushToLog() {
        GlobalScope.launch {
            //logger.log(fileLogger.getLogAsString(), null)
            //logger.wipeLog()
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

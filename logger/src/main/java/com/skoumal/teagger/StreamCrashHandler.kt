package com.skoumal.teagger

import android.content.Context
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class StreamCrashHandler internal constructor(
        var logger: StreamLogger,
        internal var fileLogger: StreamLoggerAsync = StreamLoggerAsync()
) {

    internal val loggerSync: StreamLoggerSync
        get() = if (logger is StreamLoggerSync)
            logger as StreamLoggerSync
        else
            logger.copyIntoSync()

    internal val fileLoggerSync: StreamLoggerSync
        get() = fileLogger.copyIntoSync()

    constructor(logger: StreamLogger, context: Context): this(logger) {
        try {
            val dir = File(context.filesDir, Constants.FILES_DIR).apply { mkdirs() }
            val file = File(dir, Constants.CRASH_LOG_FILE).apply { createNewFile() }
            fileLogger.setFile(file)
        } catch (t: Throwable) {
            if (t is IOException || t is SecurityException) {
                t.printStackTrace()
            } else {
                throw t
            }
        }
    }

    /**
     * Call this method in your Application class in order to log any crashes that failed to log
     * to the desired stream.
     */
    fun pushToLog() {
        fileLogger.launch {
            logger.log(fileLogger.getLogAsString(), null)
            fileLogger.wipeLog()
        }
    }

    /**
     * First attempts to log crash using the logger's [OutputStreamProvider].
     * If this action throws an exception, attempts to log into a separate file. These logs can
     * be logged into the correct stream another time by calling [pushToLog]. Finally, this method
     * throws the error again so that the application stops.
     * @param throwable the error causing the crash
     */
    fun handleCrash(throwable: Throwable) {
        runCatching {
            loggerSync.log(
                    Constants.CRASH_DEFAULT_PRIORITY,
                    Constants.CRASH_DEFAULT_TAG,
                    Constants.CRASH_DEFAULT_MESSAGE,
                    throwable
            )
        }.exceptionOrNull()?.let {
            logSeparately(throwable)
        }
    }

    private fun logSeparately(throwable: Throwable) {
        fileLoggerSync.log(
                Constants.CRASH_DEFAULT_PRIORITY,
                Constants.CRASH_DEFAULT_TAG,
                Constants.CRASH_DEFAULT_MESSAGE,
                throwable
        )
    }

    private fun StreamLogger.copyIntoSync() =
            StreamLoggerSync(outputStreamProvider, inputStreamProvider, clearFunction)
}

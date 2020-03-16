package com.skoumal.teagger

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.PrintStream

class StreamLoggerSync(
        override var outputStreamProvider: OutputStreamProvider? = null,
        override var inputStreamProvider: InputStreamProvider? = null,
        override var clearFunction: (() -> Unit)? = null,
        logEntryDelegate: LogEntryDelegate = LogEntryDelegateImpl()
) : StreamLogger, LogEntryDelegate by logEntryDelegate {

    /**
     * Adds an entry into the log file
     * @param priority values from the [android.util.Log] class
     * @param tag used to identify the source of the message
     * @param message what you would like to be logged
     * @param throwable the stacktrace of this will be printed to the log
     */
    override fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        log(entryFor(priority, tag, message), throwable)
    }

    /**
     * Adds an entry into the log file in your own format. Note: You can also pass your
     * own [LogEntryDelegate] to format your log entries
     * @param line what you would like to be logged, including your priority / tag / whatever,
     *      as you would like it to be formatted
     * @param throwable the stacktrace of this will be printed to the log
     */
    override fun log(line: String, throwable: Throwable?) {
        val outputStream = outputStreamProvider?.provideOutputStream() ?: return
        PrintStream(outputStream).use { stream ->
            stream.print(line)
            throwable?.let {
                stream.print(" ")
                throwable.printStackTrace(stream)
            }
            stream.println()
        }
    }

    override suspend fun getLogAsString(): String = withContext(Dispatchers.IO) {
        try {
            return@withContext inputStreamProvider
                ?.provideInputStream()
                ?.bufferedReader()
                ?.use { it.readText() }
                .orEmpty()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext ""
    }

    override fun wipeLog() {
        clearFunction?.invoke()
    }
}

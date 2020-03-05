package com.skoumal.teagger

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
        val outputStream = outputStreamProvider?.provideOutputStream() ?: return
        PrintStream(outputStream).use { stream ->
            stream.print(entryFor(priority, tag, message))
            throwable?.let {
                stream.print(" ")
                throwable.printStackTrace(stream)
            }
            stream.println()
        }
    }

    override suspend fun getLogAsString(): String {
        try {
            return inputStreamProvider?.provideInputStream()?.bufferedReader()?.use {
                it.readText()
            }.orEmpty()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun wipeLog() {
        clearFunction?.invoke()
    }
}

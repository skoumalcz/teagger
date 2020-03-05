package com.skoumal.teagger

import kotlinx.coroutines.*
import java.io.IOException
import java.io.PrintStream

class StreamLoggerAsync(
        override var outputStreamProvider: OutputStreamProvider? = null,
        override var inputStreamProvider: InputStreamProvider? = null,
        override var clearFunction: (() -> Unit)? = null,
        logEntryDelegate: LogEntryDelegate = LogEntryDelegateImpl(),
        coroutineScope: CoroutineScope = MainScope()
) : StreamLogger, LogEntryDelegate by logEntryDelegate, CoroutineScope by coroutineScope {

    @UseExperimental(ObsoleteCoroutinesApi::class)
    private val singleThreadContext = newSingleThreadContext("LoggerOutputContext")

    /**
     * Adds an entry into the log file
     * @param priority values from the [android.util.Log] class
     * @param tag used to identify the source of the message
     * @param message what you would like to be logged
     * @param throwable the stacktrace of this will be printed to the log
     */
    override fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        launch(singleThreadContext) {
            val outputStream = outputStreamProvider?.provideOutputStream() ?: return@launch
            PrintStream(outputStream).use { stream ->
                stream.print(entryFor(priority, tag, message))
                throwable?.let {
                    stream.print(" ")
                    throwable.printStackTrace(stream)
                }
                stream.println()
            }
        }
    }

    override suspend fun getLogAsString(): String = withContext(Dispatchers.IO) {
        try {
            return@withContext inputStreamProvider?.provideInputStream()?.bufferedReader()?.use {
                it.readText()
            }.orEmpty()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        ""
    }

    override fun wipeLog() {
        launch(singleThreadContext) {
            clearFunction?.invoke()
        }
    }
}

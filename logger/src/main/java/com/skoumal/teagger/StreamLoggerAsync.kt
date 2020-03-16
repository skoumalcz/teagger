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
    internal val singleThreadContext = newSingleThreadContext("LoggerOutputContext")

    /**
     * Adds an entry into the log file
     * @param priority values from the [android.util.Log] class
     * @param tag used to identify the source of the message
     * @param message what you would like to be logged
     * @param throwable the stacktrace of this will be printed to the log
     * @param onFinished to be called after the logging has finished no matter if there was
     *      a failure
     */
    fun log(
            priority: Int,
            tag: String,
            message: String?,
            throwable: Throwable?,
            onFinished: ((Throwable?) -> Unit)?
    ) {
        log(entryFor(priority, tag, message), throwable, onFinished)
    }

    /**
     * Adds an entry into the log file
     * @param priority values from the [android.util.Log] class
     * @param tag used to identify the source of the message
     * @param message what you would like to be logged
     * @param throwable the stacktrace of this will be printed to the log
     */
    override fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        log(priority, tag, message, throwable, null)
    }

    /**
     * Adds an entry into the log file in your own format. Note: You can also pass your
     * own [LogEntryDelegate] to format your log entries
     * @param line what you would like to be logged, including your priority / tag / whatever,
     *      as you would like it to be formatted
     * @param throwable the stacktrace of this will be printed to the log
     * @param onFinished to be called after the logging has finished no matter if there was a failure
     */
    fun log(line: String, throwable: Throwable?, onFinished: ((Throwable?) -> Unit)?) {
        launch {
            withContext(singleThreadContext) {
                runCatching {
                    val outputStream = outputStreamProvider?.provideOutputStream()
                            ?: return@runCatching
                    PrintStream(outputStream).use { stream ->
                        stream.print(line)
                        throwable?.let {
                            stream.print(" ")
                            throwable.printStackTrace(stream)
                        }
                        stream.println()
                    }
                }
            }.exceptionOrNull().let {
                onFinished?.invoke(it)
            }
        }
    }

    /**
     * Adds an entry into the log file in your own format. Note: You can also pass your
     * own [LogEntryDelegate] to format your log entries
     * @param line what you would like to be logged, including your priority / tag / whatever,
     *      as you would like it to be formatted
     * @param throwable the stacktrace of this will be printed to the log
     */
    override fun log(line: String, throwable: Throwable?) {
        log(line, throwable, null)
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
        launch(singleThreadContext) {
            clearFunction?.invoke()
        }
    }
}

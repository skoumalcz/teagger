package com.skoumal.teagger

import com.skoumal.teagger.coroutine.IOScope
import com.skoumal.teagger.crash.StreamCrashHandler
import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teagger.provider.CleanupProvider
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import com.skoumal.teagger.provider.file.FileProvider
import com.skoumal.teagger.provider.provideOrDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintStream

internal class StreamLoggerImpl(
    private val inputStream: InputStreamProvider,
    private val outputStream: OutputStreamProvider,
    private val cleanup: CleanupProvider? = null
) : StreamLogger, CoroutineScope by IOScope() {

    constructor(provider: FileProvider) : this(provider, provider)

    override var entryTransformer: LogEntryDelegate = LogEntryDelegate.default

    private val crashHandler = StreamCrashHandler(this)

    override fun log(
        priority: Int,
        tag: String,
        message: String
    ) {
        launch {
            logInternal(entryTransformer.entryFor(priority, tag, message))
        }
    }

    override fun log(throwable: Throwable) {
        launch {
            logInternal(entryTransformer.entryFor(throwable), throwable)
        }
    }

    @Synchronized
    private suspend fun logInternal(line: String) = withContext(Dispatchers.IO) {
        PrintStream(outputStream.provideOrDefault()).use { stream ->
            stream.println(line)
        }
    }

    @Synchronized
    private suspend fun logInternal(line: String, throwable: Throwable) =
        withContext(Dispatchers.IO) {
            PrintStream(outputStream.provideOrDefault()).use { stream ->
                stream.print("$line: ")
                throwable.printStackTrace(stream)
                stream.println()
            }
        }

    override suspend fun collect(): String = inputStream
        .provideInputStream()
        .bufferedReader().use { it.readText() }

    override suspend fun clear(): Boolean {
        return cleanup?.clean() ?: false
    }
}
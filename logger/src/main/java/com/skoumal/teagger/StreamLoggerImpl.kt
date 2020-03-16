package com.skoumal.teagger

import android.content.Context
import android.net.Uri
import com.skoumal.teagger.coroutine.IOScope
import com.skoumal.teagger.crash.StreamCrashHandler
import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teagger.provider.CleanupProvider
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import com.skoumal.teagger.provider.file.FileProvider
import com.skoumal.teagger.provider.provideOrDefault
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import androidx.core.content.FileProvider as AndroidFileProvider

internal class StreamLoggerImpl(
    private val inputStream: InputStreamProvider,
    private val outputStream: OutputStreamProvider,
    private val cleanup: CleanupProvider? = null
) : StreamLogger, CoroutineScope by IOScope() {

    constructor(provider: FileProvider) : this(provider, provider)

    override var entryTransformer: LogEntryDelegate = LogEntryDelegate.default

    @Volatile
    private var channel: Channel<String>? = null

    init {
        StreamCrashHandler(this)
    }

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
        getChannelForSend()?.send(line)
    }

    @Synchronized
    private suspend fun logInternal(
        line: String,
        throwable: Throwable
    ) = withContext(Dispatchers.IO) {
        PrintStream(outputStream.provideOrDefault()).use { stream ->
            stream.print("$line: ")
            throwable.printStackTrace(stream)
            stream.println()
        }
        getChannelForSend()?.let {
            val writer = StringWriter().also {
                it.write("$line: ")
            }
            PrintWriter(writer).use {
                throwable.printStackTrace(it)
            }
            it.offer(writer.toString())
        }
    }

    override suspend fun collect(context: Context, authority: Int): Uri {
        return collect(context, context.getString(authority))
    }

    override suspend fun collect(
        context: Context,
        authority: String
    ): Uri = withContext(Dispatchers.IO) {
        val sharableFile = Constants.shareFile
        inputStream.provideInputStream().use { input ->
            sharableFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        AndroidFileProvider.getUriForFile(context, authority, sharableFile)
    }

    @UseExperimental(FlowPreview::class)
    override suspend fun observe(): Flow<String> {
        return getOrCreateChannel().consumeAsFlow().also {
            inputStream.provideOrDefault().bufferedReader().useLines { lines ->
                lines.forEach { getChannelForSend()?.offer(it) }
            }
        }
    }

    override suspend fun clear(): Boolean {
        return cleanup?.clean() ?: false
    }

    // Private members

    @Synchronized
    private fun getOrCreateChannel() = channel ?: Channel<String>().also {
        channel = it
    }

    @UseExperimental(ExperimentalCoroutinesApi::class)
    private fun getChannelForSend(): Channel<String>? {
        return channel?.let {
            if (it.isClosedForSend) channel = null
            getOrCreateChannel()
        }
    }

}
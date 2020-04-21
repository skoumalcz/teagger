package com.skoumal.teagger

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.skoumal.teagger.comms.EntrySailor
import com.skoumal.teagger.coroutine.IOScope
import com.skoumal.teagger.crash.StreamCrashHandler
import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teagger.provider.CleanupProvider
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import com.skoumal.teagger.provider.file.FileProvider
import com.skoumal.teagger.provider.provideOrDefault
import com.skoumal.teanity.component.channel.Vessel
import com.skoumal.teanity.util.SubjectsToChange
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import java.io.File
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import androidx.core.content.FileProvider as AndroidFileProvider

@UseExperimental(ExperimentalCoroutinesApi::class, FlowPreview::class, SubjectsToChange::class)
internal class TeaggerImpl(
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val inputStream: InputStreamProvider,
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val outputStream: OutputStreamProvider,
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val cleanup: CleanupProvider? = null
) : Teagger, CoroutineScope by IOScope() {

    constructor(provider: FileProvider) : this(provider, provider, provider)

    override var entryTransformer: LogEntryDelegate = LogEntryDelegate.default

    @Volatile
    private var channel: BroadcastChannel<String>? = null
    private val vessel = Vessel<EntrySailor>()

    init {
        StreamCrashHandler(this)
        launch {
            drainFatal()
            vessel.dock().collect {
                when (it) {
                    is EntrySailor.Throwee -> logInternal(it.format(entryTransformer), it.throwable)
                    is EntrySailor.Log -> logInternal(it.format(entryTransformer))
                }
            }
        }
    }

    override fun log(
        priority: Int,
        tag: String,
        message: String
    ) {
        vessel.sail(EntrySailor.Log(priority, tag, message))
    }

    override fun log(throwable: Throwable) {
        vessel.sail(EntrySailor.Throwee(throwable))
    }

    private suspend fun logInternal(line: String) = withContext(Dispatchers.IO) {
        PrintStream(outputStream.provideOrDefault()).use { stream ->
            stream.println(line)
        }
        getChannelForSend()?.offer(line)
    }

    private suspend fun logInternal(
        line: String,
        throwable: Throwable
    ) = withContext(Dispatchers.IO) {
        PrintStream(outputStream.provideOrDefault()).use { stream ->
            stream.print(line)
            throwable.printStackTrace(stream)
            stream.println()
        }
        getChannelForSend()?.let {
            val writer = StringWriter().also {
                it.write(line)
            }
            PrintWriter(writer).use {
                throwable.printStackTrace(it)
            }
            it.offer(writer.toString())
        }
    }

    override suspend fun collect(): File = withContext(Dispatchers.IO) {
        Constants.shareFile.also {
            it.writeText("")
            inputStream.provideInputStream().use { input ->
                it.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override suspend fun collect(context: Context, authority: Int): Uri {
        return collect(context, context.getString(authority))
    }

    override suspend fun collect(
        context: Context,
        authority: String
    ): Uri = AndroidFileProvider.getUriForFile(context, authority, collect())

    override suspend fun observe(): Flow<String> {
        // fixme this is broken when trying to fill the sub with result of collect(), since client cannot consume more or same than the broadcast offers
        return getOrCreateChannel().openSubscription().consumeAsFlow()
    }

    override suspend fun clear(): Boolean = withContext(Dispatchers.IO) {
        cleanup?.clean() ?: false
    }

    // Private members

    @Synchronized
    private fun getOrCreateChannel() = channel ?: BroadcastChannel<String>(BUFFERED).also {
        channel = it
    }

    private fun getChannelForSend(): BroadcastChannel<String>? {
        return channel?.let {
            if (it.isClosedForSend) {
                channel = null
            }
            channel
        }
    }

    /**
     * Drains fatal file to the regular crash file provided during object init. Once this process
     * succeeds, the fatal file is deleted with all of it's contents trusted in the hands of the
     * host app's implementation.
     *
     * This is the only thing that *MUST NOT* crash under any circumstances. This will undoubtfully
     * crash the host app during Application init, and we cannot possibly allow that.
     * */
    private suspend fun drainFatal() = withContext(Dispatchers.IO) {
        runCatching {
            PrintStream(outputStream.provideOutputStream()).use { output ->
                Constants.fatalFile.inputStream().use { input ->
                    input.bufferedReader().forEachLine {
                        output.appendln(it)
                    }
                }
            }
            Constants.fatalFile.deleteRecursively()
        }.onFailure {
            Log.e(
                TAG,
                "Draining fatal error resulted in error. Both streams must be accessible!",
                it
            )
        }
    }

}
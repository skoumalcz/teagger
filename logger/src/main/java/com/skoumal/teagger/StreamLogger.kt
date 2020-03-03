package com.skoumal.teagger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.*

class StreamLogger(
        var outputStreamProvider: OutputStreamProvider? = null,
        var inputStreamProvider: InputStreamProvider? = null,
        var clearFunction: (() -> Unit)? = null
) {

    companion object {
        private const val CACHE_DIR = "teagger/"
        private const val CACHE_SHARED_FILE = "sharedlog.txt"
    }

    /**
     * Adds an entry into the log file
     * @param priority values from the [android.util.Log] class
     * @param tag
     * @param message
     * @param throwable
     */
    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        runCatching {
            val outputStream = outputStreamProvider?.provideOutputStream() ?: return@runCatching
            PrintStream(outputStream).use { stream ->
                stream.print(entryFor(priority, tag, message))
                runCatching {
                    throwable?.let {
                        stream.print(" ")
                        throwable.printStackTrace(stream)
                    }
                }
                stream.println()
            }
        }
    }

    fun v(tag: String, message: String?, throwable: Throwable?) =
            log(Log.VERBOSE, tag, message, throwable)

    fun d(tag: String, message: String?, throwable: Throwable?) =
            log(Log.DEBUG, tag, message, throwable)

    fun i(tag: String, message: String?, throwable: Throwable?) =
            log(Log.INFO, tag, message, throwable)

    fun w(tag: String, message: String?, throwable: Throwable?) =
            log(Log.WARN, tag, message, throwable)

    fun e(tag: String, message: String?, throwable: Throwable?) =
            log(Log.ERROR, tag, message, throwable)

    fun wtf(tag: String, message: String?, throwable: Throwable?) =
            log(Log.ASSERT, tag, message, throwable)

    /**
     * Opens a share activity for a log file.
     * @param context can be application context, will be used to get the URI and start the activity
     * @param authority authority of the [androidx.core.content.FileProvider] that has access to the teagger/ directory in cache
     */
    fun shareLog(context: Context, authority: String) {
        val file = getFileForSharing(context) ?: return

        val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun getLogAsString() = runCatching {
        inputStreamProvider?.provideInputStream()?.bufferedReader()?.use {
            it.readText()
        }
    }.getOrNull().orEmpty()

    fun wipeLog() = runCatching {
        clearFunction?.invoke()
    }

    internal fun entryFor(
            priority: Int,
            tag: String,
            message: String?
    ): String {
        val priorityString = when (priority) {
            Log.ASSERT -> "A"
            Log.DEBUG -> "D"
            Log.ERROR -> "E"
            Log.INFO -> "I"
            Log.VERBOSE -> "V"
            Log.WARN -> "W"
            else -> ""
        }

        return "$priorityString/$tag: ${message.orEmpty()}"
    }

    internal fun getFileForSharing(context: Context) = runCatching {
        val string = getLogAsString()
        val dir = File(context.cacheDir, CACHE_DIR).apply {
            mkdirs()
        }
        File(dir, CACHE_SHARED_FILE).apply {
            delete()
            createNewFile()
            outputStream().bufferedWriter().use {
                it.write(string)
            }
        }
    }.getOrNull()
}

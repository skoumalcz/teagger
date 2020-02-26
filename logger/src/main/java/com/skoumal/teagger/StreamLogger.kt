package com.skoumal.teagger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.*
import androidx.core.content.FileProvider as AndroidFileProvider

class StreamLogger(
        var outputStreamProvider: OutputStreamProvider?,
        var inputStreamProvider: InputStreamProvider?,
        var clearFunction: (() -> Unit)?
) {

    constructor() : this(null, null, null)

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
            outputStreamProvider?.provideOutputStream()?.bufferedWriter()?.use {
                it.write(entryFor(priority, tag, message, throwable))
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

        val contentUri: Uri = AndroidFileProvider.getUriForFile(context, authority, file)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    internal fun entryFor(
            priority: Int,
            tag: String,
            message: String?,
            throwable: Throwable?
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

        val basicEntry = "$priorityString/$tag: ${message.orEmpty()}"
        var entry = ""
        runCatching {
            throwable?.let {
                entry = "$basicEntry ${it.getStackTraceString()}\n"
            } ?: "$basicEntry\n".let { entry = it }
        }

        return entry
    }

    internal fun wipeLog() = clearFunction?.invoke()

    internal fun getLogAsString() = runCatching {
        inputStreamProvider?.provideInputStream()?.bufferedReader()?.use {
            it.readText()
        }
    }.getOrNull().orEmpty()

    internal fun getFileForSharing(context: Context) = runCatching {
        val string = getLogAsString()
        val dir = File(context.cacheDir, CACHE_DIR)
        dir.mkdirs()
        val file = File(dir, CACHE_SHARED_FILE)
        file.delete()
        file.createNewFile()
        file.outputStream().bufferedWriter().use {
            it.write(string)
        }
        file
    }.getOrNull()

    /**
     * Testable alternative to [Log.getStackTraceString]
     */
    internal fun Throwable.getStackTraceString(): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter, false)
        printStackTrace(printWriter)
        printWriter.close()
        return stringWriter.toString()
    }

}

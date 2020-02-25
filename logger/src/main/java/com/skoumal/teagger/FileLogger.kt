package com.skoumal.teagger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class FileLogger(context: Context, authority: String) {

    companion object {
        private const val LOG_DIRECTORY = "teagger"
        private const val LOG_FILENAME = "teagger_log.txt"
    }

    internal var file: File? = null
        private set

    internal var filesDir: File
        private set

    internal var authority: String
        private set

    init {
        filesDir = context.filesDir
        this.authority = authority
    }

    /**
     * Adds an entry into the log file
     * @param priority values from the [android.util.Log] class
     * @param tag
     * @param message
     * @param throwable
     */
    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        val outputFile = file ?: createFile() ?: return
        outputFile.appendText(entryFor(priority, tag, message, throwable))
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
     * Opens a share activity for the log file.
     * @param context can be application context, will be used to get the URI and start the activity
     */
    fun shareLog(context: Context) {
        val sharedFile = file ?: return
        val contentUri: Uri = FileProvider.getUriForFile(context, authority, sharedFile)
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

    internal fun File.createLogDir() = runCatching {
        mkdirs()
        this
    }.getOrNull()

    internal fun File.createLog() = runCatching {
        createNewFile()
        file = this
        file
    }.getOrNull()

    private fun createFile() = File(filesDir, LOG_DIRECTORY).createLogDir()?.let {
        File(it, LOG_FILENAME).createLog()
    }

    internal fun wipeLog() {
        runCatching {
            file?.delete()
            file?.createNewFile()
        }
    }

    internal fun getLogAsString() = runCatching {
        file?.readText()
    }.getOrNull().orEmpty()

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

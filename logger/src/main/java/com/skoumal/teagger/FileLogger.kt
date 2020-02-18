package com.skoumal.teagger

import android.content.Context
import android.util.Log
import java.io.File

object FileLogger {

    private const val LOG_FILENAME = "teagger_log.txt"

    private var file: File? = null

    fun init(context: Context) {
        try {
            file = File(context.filesDir, LOG_FILENAME).apply {
                if (exists().not())
                    createNewFile()
            }
        } catch (ignored: Exception) {}
    }

    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        val outputFile = file ?: return

        val priorityString = when (priority) {
            Log.ASSERT -> "A"
            Log.DEBUG -> "D"
            Log.ERROR -> "E"
            Log.INFO -> "I"
            Log.VERBOSE -> "V"
            Log.WARN -> "W"
            else -> ""
        }

        val entry = "$priorityString/$tag: ${message.orEmpty()}"
        try {
            throwable?.let {
                outputFile.appendText("$entry ${Log.getStackTraceString(it)}\n")
            } ?: outputFile.appendText("$entry\n")
        } catch (ignored: Exception) {
        }
    }

    fun wipeLog() {
        try {
            file?.delete()
        } catch (ignored: Exception) {
        }
    }
}

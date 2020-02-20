package com.skoumal.teagger

import android.content.Context
import android.util.Log
import java.io.File

object FileLogger {

    private const val LOG_DIRECTORY = "teagger"
    private const val LOG_FILENAME = "teagger_log.txt"

    internal var file: File? = null
        private set

    internal lateinit var authority: String

    /**
     * Call this method before any call to [log] or opening LoggerActivity, preferably in your Application implementation
     * @param context can be application context, will be used to access internal files directory
     * @param authority the authority to a FileProvider with access to the teagger/ directory in internal files dir
     */
    fun init(context: Context, authority: String) {
        this.authority = authority
        try {
            val dir = File(context.filesDir, LOG_DIRECTORY)
            dir.mkdirs()
            file = File(dir, LOG_FILENAME).apply {
                if (exists().not())
                    createNewFile()
            }
        } catch (ignored: Exception) {
        }
    }

    /**
     * This method will not have any effect unless {@link #init} has been called.
     * @param priority values from the [android.util.Log] class
     * @param tag
     * @param message
     * @param throwable
     */
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

    internal fun wipeLog() {
        try {
            file?.delete()
            file?.createNewFile()
        } catch (ignored: Exception) {
        }
    }

    internal fun getLogAsString(): String {
        try {
            return file?.readText().orEmpty()
        } catch (ignored: Exception) {
        }
        return ""
    }
}

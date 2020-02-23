package com.skoumal.teagger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

object FileLogger {

    private const val LOG_DIRECTORY = "teagger"
    private const val LOG_FILENAME = "teagger_log.txt"

    internal var file: File? = null

    internal var filesDir: File? = null

    internal lateinit var authority: String
        private set

    /**
     * Call this method before any call to [log] or opening LoggerActivity,
     * preferably in your Application implementation
     * @param context can be application context, will be used to access internal files directory
     * @param authority the authority to a FileProvider with access to the teagger/ directory in internal files dir
     */
    fun init(context: Context, authority: String) {
        filesDir = context.filesDir
        this.authority = authority
    }

    /**
     * This method will not have any effect unless {@link #init} has been called.
     * @param priority values from the [android.util.Log] class
     * @param tag
     * @param message
     * @param throwable
     */
    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        val outputFile = file ?: createFile() ?: return
        outputFile.appendText(entryFor(priority, tag, message, throwable))
    }

    internal fun entryFor(priority: Int, tag: String, message: String?, throwable: Throwable?): String {
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

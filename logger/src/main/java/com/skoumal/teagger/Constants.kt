package com.skoumal.teagger

import android.util.Log
import java.io.File

object Constants {
    internal const val CACHE_DIR = "teagger/"
    internal const val CACHE_SHARED_FILE = "sharedlog.txt"
    internal const val FILES_DIR = "teagger/"
    internal const val CRASH_LOG_FILE = "crashlog.txt"
    internal const val FATAL_LOG_FILE = "fatal.txt"

    const val CRASH_DEFAULT_PRIORITY = Log.ERROR
    const val CRASH_DEFAULT_TAG = "Crash"
    const val CRASH_DEFAULT_MESSAGE = "The application has crashed: "

    internal val fatalFile get() = File(StreamLogger.context.filesDir, FATAL_LOG_FILE)
}

package com.skoumal.teagger

import android.util.Log

object Constants {
    internal const val CACHE_DIR = "teagger/"
    internal const val CACHE_SHARED_FILE = "sharedlog.txt"
    internal const val FILES_DIR = "teagger/"
    internal const val CRASH_LOG_FILE = "crashlog.txt"

    const val CRASH_DEFAULT_PRIORITY = Log.ERROR
    const val CRASH_DEFAULT_TAG = "Crash"
    const val CRASH_DEFAULT_MESSAGE = "The application has crashed: "
}

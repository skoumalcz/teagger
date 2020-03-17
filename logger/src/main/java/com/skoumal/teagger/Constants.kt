package com.skoumal.teagger

import android.util.Log
import java.io.File

object Constants {

    private const val INTERNAL_DIR = "teagger"
    private const val CACHE_SHARED_FILE = "sharedlog.txt"
    private const val CRASH_LOG_FILE = "crashlog.txt"
    private const val FATAL_LOG_FILE = "fatal.txt"

    const val CRASH_DEFAULT_PRIORITY = Log.ERROR
    const val CRASH_DEFAULT_TAG = "Crash"
    const val CRASH_DEFAULT_MESSAGE = "The application has crashed"

    private val cacheFolder
        get() = File(StreamLogger.context.cacheDir, INTERNAL_DIR).createFoldersIfMissing()
    private val fileFolder
        get() = File(StreamLogger.context.filesDir, INTERNAL_DIR).createFoldersIfMissing()

    internal val fatalFile get() = File(fileFolder, FATAL_LOG_FILE).createFileIfMissing()
    internal val crashFile get() = File(fileFolder, CRASH_LOG_FILE).createFileIfMissing()
    internal val shareFile get() = File(cacheFolder, CACHE_SHARED_FILE).createFileIfMissing()

    private fun File.createFileIfMissing() = apply {
        if (!exists()) createNewFile()
    }

    private fun File.createFoldersIfMissing() = apply {
        if (!exists()) mkdirs()
    }

}

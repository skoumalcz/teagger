package com.skoumal.teagger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

fun StreamLogger.setFile(file: File) {
    val fileProvider = FileProvider(file)
    inputStreamProvider = fileProvider
    outputStreamProvider = fileProvider
    clearFunction = fileProvider.provideCleanFunction()
}

suspend fun StreamLogger.getFileForSharing(context: Context): File? {
    val string = getLogAsString()
    try {
        val dir = File(context.cacheDir, Constants.CACHE_DIR).apply {
            mkdirs()
        }
        return File(dir, Constants.CACHE_SHARED_FILE).apply {
            delete()
            createNewFile()
            outputStream().bufferedWriter().use {
                it.write(string)
            }
        }
    } catch (e: Exception) {
        if (e is SecurityException || e is IOException || e is FileNotFoundException) {
            e.printStackTrace()
        } else {
            throw e
        }
    }
    return null
}

/**
 * Opens a share activity for a log file.
 * @param scope the [kotlinx.coroutines.CoroutineScope] that will be used to create the file
 * @param context can be application context, will be used to get the URI and start the activity
 * @param authority authority of the [androidx.core.content.FileProvider] that has access to the teagger/ directory in cache
 */
fun StreamLogger.shareLog(scope: CoroutineScope, context: Context, authority: String) {
    scope.launch(Dispatchers.Main) {
        val file = withContext(Dispatchers.IO) {
            getFileForSharing(context)
        } ?: return@launch

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
}

fun StreamLogger.v(tag: String, message: String?, throwable: Throwable?) =
        log(Log.VERBOSE, tag, message, throwable)

fun StreamLogger.d(tag: String, message: String?, throwable: Throwable?) =
        log(Log.DEBUG, tag, message, throwable)

fun StreamLogger.i(tag: String, message: String?, throwable: Throwable?) =
        log(Log.INFO, tag, message, throwable)

fun StreamLogger.w(tag: String, message: String?, throwable: Throwable?) =
        log(Log.WARN, tag, message, throwable)

fun StreamLogger.e(tag: String, message: String?, throwable: Throwable?) =
        log(Log.ERROR, tag, message, throwable)

fun StreamLogger.wtf(tag: String, message: String?, throwable: Throwable?) =
        log(Log.ASSERT, tag, message, throwable)

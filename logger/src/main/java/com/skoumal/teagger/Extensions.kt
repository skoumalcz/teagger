package com.skoumal.teagger

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Opens a share activity for a log file.
 * @param scope the [kotlinx.coroutines.CoroutineScope] that will be used to create the file
 * @param context can be application context, will be used to get the URI and start the activity
 * @param authority authority of the [androidx.core.content.FileProvider] that has access to the teagger/ directory in cache
 */
suspend fun StreamLogger.shareLog(
    context: Context,
    authority: String
) = withContext(Dispatchers.Main) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, collect(context, authority))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
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

private fun StreamLogger.log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
    if (message != null) {
        log(priority, tag, message)
    }
    if (throwable != null) {
        log(throwable)
    }
}

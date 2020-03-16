package com.skoumal.teagger.entry

import android.util.Log
import com.skoumal.teagger.Constants

internal class LogEntryDelegateImpl : LogEntryDelegate {

    override fun entryFor(priority: Int, tag: String, message: String): String {
        val priorityString = when (priority) {
            Log.ASSERT -> "A"
            Log.DEBUG -> "D"
            Log.ERROR -> "E"
            Log.INFO -> "I"
            Log.VERBOSE -> "V"
            Log.WARN -> "W"
            else -> ""
        }

        return "$priorityString/$tag: ${message.ifBlank { "{Blank error message}" }}"
    }

    override fun entryFor(throwable: Throwable): String {
        return "E/${throwable.message ?: Constants.CRASH_DEFAULT_MESSAGE}"
    }

}
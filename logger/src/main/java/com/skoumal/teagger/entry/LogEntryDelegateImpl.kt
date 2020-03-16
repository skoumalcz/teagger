package com.skoumal.teagger.entry

import android.util.Log
import com.skoumal.teagger.Constants
import java.text.DateFormat
import java.util.*

internal class LogEntryDelegateImpl : LogEntryDelegate {

    private val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)

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

        return "${format.format(Date())} - $priorityString/$tag: ${message.ifBlank { "{Blank error message}" }}"
    }

    override fun entryFor(throwable: Throwable): String {
        return "${format.format(Date())} - E/${throwable.message ?: Constants.CRASH_DEFAULT_MESSAGE}"
    }

}
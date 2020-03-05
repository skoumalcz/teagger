package com.skoumal.teagger

import android.util.Log

class LogEntryDelegateImpl : LogEntryDelegate {

    override fun entryFor(
            priority: Int,
            tag: String,
            message: String?
    ): String {
        val priorityString = when (priority) {
            Log.ASSERT -> "A"
            Log.DEBUG -> "D"
            Log.ERROR -> "E"
            Log.INFO -> "I"
            Log.VERBOSE -> "V"
            Log.WARN -> "W"
            else -> ""
        }

        return "$priorityString/$tag: ${message.orEmpty()}"
    }
}
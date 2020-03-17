package com.skoumal.teagger.entry

import android.util.Log
import com.skoumal.teagger.Constants
import java.text.DateFormat

internal class LogEntryDelegateImpl : LogEntryDelegate {

    override fun entryFor(priority: Int, tag: String, message: String): String {
        val priorityString = priorities.getOrElse(priority) { priorityName }

        return StringBuilder(priorityString)
            .append(tag)
            .append(" (")
            .append(System.currentTimeMillis())
            .append("): ")
            .append(message.ifBlank { "{Blank error message}" })
            .toString()
    }

    override fun entryFor(throwable: Throwable): String {
        return StringBuilder(priorities[Log.ERROR] ?: error("Cannot find log priority"))
            .append(Constants.CRASH_DEFAULT_MESSAGE)
            .append(" (")
            .append(System.currentTimeMillis())
            .append("): ")
            .toString()
    }

    override fun resolvePriorityForEntry(line: String): Int {
        // special case for logging throwables
        if (line.trimStart().startsWith("at")) return Log.ERROR

        priorities.forEach {
            if (line.startsWith(it.value)) return it.key
        }
        return -1
    }

    companion object {
        private val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
        private const val priorityName = "Default/"
        private val priorities = mapOf(
            Log.ASSERT to "A/",
            Log.DEBUG to "D/",
            Log.ERROR to "E/",
            Log.INFO to "I/",
            Log.VERBOSE to "V/",
            Log.WARN to "W/"
        )
    }

}
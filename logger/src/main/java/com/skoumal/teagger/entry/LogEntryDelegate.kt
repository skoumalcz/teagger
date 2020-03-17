package com.skoumal.teagger.entry

interface LogEntryDelegate {

    fun entryFor(priority: Int, tag: String, message: String): String
    fun entryFor(throwable: Throwable): String
    fun resolvePriorityForEntry(entry: String): Int

    companion object {
        val default: LogEntryDelegate get() = LogEntryDelegateImpl()
    }

}

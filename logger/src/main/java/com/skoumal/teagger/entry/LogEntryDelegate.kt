package com.skoumal.teagger.entry

interface LogEntryDelegate {
    fun entryFor(priority: Int, tag: String, message: String?): String
}

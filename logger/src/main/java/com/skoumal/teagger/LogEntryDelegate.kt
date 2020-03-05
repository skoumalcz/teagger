package com.skoumal.teagger

interface LogEntryDelegate {
    fun entryFor(priority: Int, tag: String, message: String?): String
}

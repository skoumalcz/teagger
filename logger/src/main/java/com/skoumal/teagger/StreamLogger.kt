package com.skoumal.teagger

interface StreamLogger {
    var outputStreamProvider: OutputStreamProvider?
    var inputStreamProvider: InputStreamProvider?
    var clearFunction: (() -> Unit)?

    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?)
    fun log(line: String, throwable: Throwable?)

    suspend fun getLogAsString(): String
    fun wipeLog()
}

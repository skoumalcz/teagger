package com.skoumal.teagger

import kotlinx.coroutines.Dispatchers


interface StreamLogger {
    var outputStreamProvider: OutputStreamProvider?
    var inputStreamProvider: InputStreamProvider?
    var clearFunction: (() -> Unit)?

    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?)
    fun log(line: String, throwable: Throwable?)

    /**
     * Fetching log is virtually impossible to achieve with anything but IO work, hence it always
     * runs on [Dispatchers.IO].
     * */
    suspend fun getLogAsString(): String
    fun wipeLog()
}

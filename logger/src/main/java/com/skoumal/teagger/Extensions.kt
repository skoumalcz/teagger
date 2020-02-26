package com.skoumal.teagger

import java.io.File

fun StreamLogger.setFile(file: File) {
    val fileProvider = FileProvider(file)
    inputStreamProvider = fileProvider
    outputStreamProvider = fileProvider
    clearFunction = fileProvider.provideCleanFunction()
}

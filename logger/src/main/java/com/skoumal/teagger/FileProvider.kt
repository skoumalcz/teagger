package com.skoumal.teagger

import java.io.File
import java.io.FileOutputStream

class FileProvider(val file: File) : OutputStreamProvider, InputStreamProvider {

    override fun provideOutputStream() = runCatching {
        FileOutputStream(file, true)
    }.getOrNull()

    override fun provideInputStream() = runCatching { file.inputStream() }.getOrNull()

    fun provideCleanFunction(): () -> Unit = {
        runCatching {
            file.delete()
            file.createNewFile()
        }
    }
}

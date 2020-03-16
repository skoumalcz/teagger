package com.skoumal.teagger.provider.file

import com.skoumal.teagger.provider.CleanupProvider
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import java.io.File

class FileProvider(
    private val file: File
) : CleanupProvider,
    OutputStreamProvider by FileOutputStreamProvider(file),
    InputStreamProvider by FileInputStreamProvider(file) {

    override fun clean(): Boolean {
        return try {
            file.delete()
        } catch (e: SecurityException) {
            false
        }
    }

}

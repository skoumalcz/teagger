package com.skoumal.teagger.provider.file

import com.skoumal.teagger.provider.InputStreamProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

internal class FileInputStreamProvider(
    private val file: File
) : InputStreamProvider {

    override fun provideInputStream(): InputStream {
        if (!file.exists()) {
            file.createNewFile()
        }
        return try {
            file.inputStream()
        } catch (e: FileNotFoundException) {
            // todo log this separately as "file doesn't exist"
            throw e
        } catch (e: NullPointerException) {
            // todo log this separately as "file has null name"
            throw e
        } catch (e: SecurityException) {
            // todo log this separately as "we cannot access this file"
            throw e
        }
    }

}
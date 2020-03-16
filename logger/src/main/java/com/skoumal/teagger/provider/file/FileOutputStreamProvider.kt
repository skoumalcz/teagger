package com.skoumal.teagger.provider.file

import com.skoumal.teagger.provider.OutputStreamProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

internal class FileOutputStreamProvider(
    private val file: File
) : OutputStreamProvider {
    override fun provideOutputStream(): OutputStream {
        if (!file.exists()) {
            file.createNewFile()
        }
        return try {
            FileOutputStream(file, true)
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
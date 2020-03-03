package com.skoumal.teagger

import java.io.*

class FileProvider(val file: File) : OutputStreamProvider, InputStreamProvider {

    override fun provideOutputStream(): OutputStream? {
        try {
            return FileOutputStream(file, true)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    override fun provideInputStream(): InputStream? {
        try {
            return FileInputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun provideCleanFunction(): () -> Unit = {
        try {
            file.delete()
            file.createNewFile()
        } catch (e: Exception) {
            if (e is SecurityException || e is IOException) {
                e.printStackTrace()
            } else {
                throw e
            }
        }
    }
}

package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.setFile
import java.io.File

class MyApplication : Application() {

    companion object {
        lateinit var streamLogger: StreamLogger
    }

    override fun onCreate() {
        super.onCreate()
        streamLogger = StreamLogger()

        val file = File(filesDir, "teaggerlog")
        file.createNewFile()

        streamLogger.setFile(file)
    }
}

package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.StreamLoggerAsync
import com.skoumal.teagger.setFile
import java.io.File

class MyApplication : Application() {

    companion object {
        lateinit var streamLogger: StreamLogger
    }

    override fun onCreate() {
        super.onCreate()
        streamLogger = StreamLoggerAsync()

        val file = File(filesDir, "teaggerlog")
        file.createNewFile()

        streamLogger.setFile(file)
    }
}

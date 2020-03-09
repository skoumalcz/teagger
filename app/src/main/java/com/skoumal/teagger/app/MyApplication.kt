package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.*
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

        val crashHandler = StreamCrashHandler(streamLogger, this)
        crashHandler.pushToLog()

        /*val crashingLogger = StreamLoggerSync().apply {
            outputStreamProvider = object : OutputStreamProvider {
                override fun provideOutputStream(): OutputStream? {
                    throw IllegalArgumentException()
                }
            }

            inputStreamProvider = object : InputStreamProvider {
                override fun provideInputStream(): InputStream? = "".byteInputStream()
            }

            clearFunction = {}
         }

        val crashingCrashHandler = StreamCrashHandler(crashingLogger, this)*/

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            crashHandler.handleCrash(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}

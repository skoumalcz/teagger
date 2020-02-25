package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.FileLogger

class MyApplication : Application() {

    companion object {
        lateinit var fileLogger: FileLogger
    }

    override fun onCreate() {
        super.onCreate()
        fileLogger = FileLogger(this, "com.teagger.fileprovider")
    }
}

package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.FileLogger

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FileLogger.init(this, "com.teagger.fileprovider")
    }

}

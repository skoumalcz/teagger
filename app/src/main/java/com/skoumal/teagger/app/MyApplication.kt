package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.timber.TeaggerTree
import timber.log.Timber

class MyApplication : Application() {

    companion object {
        lateinit var streamLogger: StreamLogger
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.plant(TeaggerTree())
        streamLogger = StreamLogger(this)
    }
}

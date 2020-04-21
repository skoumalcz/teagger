package com.skoumal.teagger.app

import android.app.Application
import com.skoumal.teagger.Teagger
import com.skoumal.teagger.timber.TeaggerTree
import timber.log.Timber

class MyApplication : Application() {

    companion object {
        lateinit var teagger: Teagger
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.plant(TeaggerTree())
        teagger = Teagger.start(this) {}
    }
}

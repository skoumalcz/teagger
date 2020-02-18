package com.skoumal.teagger.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skoumal.teagger.FileLogger
import java.io.File
import java.util.*
import kotlin.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 0 until 100) {
            FileLogger.log(Log.DEBUG, "MainActivity", "message $i", null)
        }

        try {
            throw IllegalArgumentException("test")
        } catch (e: IllegalArgumentException) {
            FileLogger.log(Log.ERROR, "MainActivity", null, e)
        }

        val f = File(filesDir, "teagger_log.txt")
        val s = Scanner(f)
        while (s.hasNextLine()) {
            Log.d("logger", s.nextLine())
        }

        FileLogger.wipeLog()
    }
}

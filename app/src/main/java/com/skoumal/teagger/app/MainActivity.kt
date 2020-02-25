package com.skoumal.teagger.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*
import kotlin.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 0 until 100) {
            MyApplication.fileLogger.log(Log.DEBUG, "MainActivity", "message $i", null)
        }

        try {
            throw IllegalArgumentException("test")
        } catch (e: IllegalArgumentException) {
            MyApplication.fileLogger.log(Log.ERROR, "MainActivity", null, e)
        }

        val f = File(filesDir, "teagger/teagger_log.txt")
        val s = Scanner(f)
        while (s.hasNextLine()) {
            Log.d("logger", s.nextLine())
        }

        startActivity(Intent(this, MyLoggerActivity::class.java))
    }
}

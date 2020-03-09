package com.skoumal.teagger.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        for (i in 0 until 100) {
            MyApplication.streamLogger.log(Log.DEBUG, "MainActivity", "message $i", null)
        }

        try {
            throw IllegalArgumentException("test")
        } catch (e: IllegalArgumentException) {
            MyApplication.streamLogger.log(Log.ERROR, "MainActivity", null, e)
        }

        val f = File(filesDir, "teaggerlog")
        val s = Scanner(f)
        while (s.hasNextLine()) {
            Log.d("logger", s.nextLine())
        }

        main_crash.setOnClickListener { 0 / 0 }
        main_share.setOnClickListener {
            startActivity(Intent(this, MyLoggerActivity::class.java))
        }
    }
}

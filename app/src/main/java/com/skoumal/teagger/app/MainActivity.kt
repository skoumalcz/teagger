package com.skoumal.teagger.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skoumal.teagger.ui.LoggerActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        for (i in 0 until 100) {
            Timber.d("message $i")
        }

        try {
            throw IllegalArgumentException("test")
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
        }

        main_crash.setOnClickListener { 0 / 0 }
        main_share.setOnClickListener {
            startActivity(Intent(this, LoggerActivity::class.java))
        }
    }
}

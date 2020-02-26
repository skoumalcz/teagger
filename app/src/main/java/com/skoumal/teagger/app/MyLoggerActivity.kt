package com.skoumal.teagger.app

import com.skoumal.teagger.LoggerActivity
import com.skoumal.teagger.LoggerViewModel

class MyLoggerActivity : LoggerActivity() {

    override val streamLogger = MyApplication.streamLogger

    override val viewModel = LoggerViewModel(streamLogger)
}
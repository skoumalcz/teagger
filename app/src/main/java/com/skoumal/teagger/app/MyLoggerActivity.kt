package com.skoumal.teagger.app

import com.skoumal.teagger.LoggerActivity
import com.skoumal.teagger.LoggerViewModel

class MyLoggerActivity : LoggerActivity() {

    override val fileLogger = MyApplication.fileLogger

    override val viewModel = LoggerViewModel(fileLogger)
}
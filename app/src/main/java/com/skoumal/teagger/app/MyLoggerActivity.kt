package com.skoumal.teagger.app

import com.skoumal.teagger.ui.LoggerActivity
import com.skoumal.teagger.ui.LoggerViewModel

class MyLoggerActivity : LoggerActivity() {

    override val streamLogger = MyApplication.streamLogger

    override val viewModel = LoggerViewModel(streamLogger, "com.teagger.fileprovider")
}

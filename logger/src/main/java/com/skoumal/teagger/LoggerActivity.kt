package com.skoumal.teagger

import com.skoumal.teagger.databinding.ActivityLoggerBinding
import com.skoumal.teanity.view.TeanityActivity

abstract class LoggerActivity : TeanityActivity<LoggerViewModel, ActivityLoggerBinding>() {
    override val layoutRes = R.layout.activity_logger
    abstract override val viewModel: LoggerViewModel

    abstract val fileLogger: FileLogger
}

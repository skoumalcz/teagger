package com.skoumal.teagger.ui

import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.ui.databinding.ActivityLoggerBinding
import com.skoumal.teanity.view.TeanityActivity

abstract class LoggerActivity : TeanityActivity<LoggerViewModel, ActivityLoggerBinding>() {
    override val layoutRes = R.layout.activity_logger
    abstract override val viewModel: LoggerViewModel

    abstract val streamLogger: StreamLogger

    override fun onDestroy() {
        super.onDestroy()
        viewModel.refreshJob.cancel()
    }
}

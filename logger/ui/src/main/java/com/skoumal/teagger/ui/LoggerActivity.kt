package com.skoumal.teagger.ui

import com.skoumal.teagger.ui.databinding.ActivityLoggerBinding
import com.skoumal.teanity.view.TeanityActivity

class LoggerActivity : TeanityActivity<LoggerViewModel, ActivityLoggerBinding>() {
    override val layoutRes = R.layout.activity_logger
    override val viewModel = LoggerViewModel()
}

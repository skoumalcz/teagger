package com.skoumal.teagger

import com.skoumal.teagger.databinding.ActivityLoggerBinding
import com.skoumal.teanity.view.TeanityActivity

/**
 * Do not open this activity unless [FileLogger.init] has been called first.
 */
class LoggerActivity : TeanityActivity<LoggerViewModel, ActivityLoggerBinding>() {
    override val layoutRes = R.layout.activity_logger
    override val viewModel = LoggerViewModel()
}

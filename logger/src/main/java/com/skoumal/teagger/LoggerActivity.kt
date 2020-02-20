package com.skoumal.teagger

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import com.skoumal.teagger.databinding.ActivityLoggerBinding
import com.skoumal.teanity.view.TeanityActivity
import com.skoumal.teanity.viewevent.base.ViewEvent

class LoggerActivity : TeanityActivity<LoggerViewModel, ActivityLoggerBinding>() {
    override val layoutRes = R.layout.activity_logger
    override val viewModel = LoggerViewModel()

    override fun onEventDispatched(event: ViewEvent) {
        when (event) {
            is LoggerViewModel.FinishActivityEvent -> finish()
            is LoggerViewModel.SendLogEvent -> sendLog(event)
        }

        super.onEventDispatched(event)
    }

    private fun sendLog(event: LoggerViewModel.SendLogEvent) {
        val contentUri: Uri = getUriForFile(this, FileLogger.authority, event.file)

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}

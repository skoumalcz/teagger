package com.skoumal.teagger.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.shareLog
import com.skoumal.teanity.databinding.GenericRvItem
import com.skoumal.teanity.extensions.bindingOf
import com.skoumal.teanity.extensions.compareToSafe
import com.skoumal.teanity.extensions.diffListOf
import com.skoumal.teanity.viewevent.base.ActivityExecutor
import com.skoumal.teanity.viewevent.base.ContextExecutor
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel
import kotlinx.coroutines.*

/**
 * @param streamLogger The [StreamLogger] which has been used for logging
 * @param authority The [androidx.core.content.FileProvider] authority which has access to the teagger/ directory in cache
 */
class LoggerViewModel(
    private val streamLogger: StreamLogger,
    private val authority: String
) : TeanityViewModel() {

    val binding = bindingOf<LogLineItem> { }
    val items = diffListOf<LogLineItem>()

    val refreshJob: Job

    init {
        refreshJob = launch {
            while (isActive) {
                refreshLog()
                delay(4000)
            }
        }
    }

    private fun refreshLog() = launch {
        streamLogger.getLogAsString().split('\n').map { LogLineItem(it) }.let {
            items.updateAsync(it)
        }
    }

    fun sendLog() {
        SendLogEvent(streamLogger, authority, this).publish()
    }

    fun wipeLog() {
        streamLogger.wipeLog()
        FinishActivityEvent.publish()
    }

    class SendLogEvent(
        private val streamLogger: StreamLogger,
        private val authority: String,
        private val scope: CoroutineScope
    ) : ViewEvent(), ContextExecutor {

        override fun invoke(context: Context) {
            scope.launch {
                streamLogger.shareLog(context, authority)
            }
        }
    }

    object FinishActivityEvent : ViewEvent(), ActivityExecutor {
        override fun invoke(activity: AppCompatActivity) {
            activity.finish()
        }
    }

    class LogLineItem(val text: String) : GenericRvItem() {

        override val layoutRes = R.layout.item_log_line

        override fun contentSameAs(other: GenericRvItem) = compareToSafe<LogLineItem> {
            it.text == text
        }

        override fun sameAs(other: GenericRvItem) = compareToSafe<LogLineItem> { true }
    }
}

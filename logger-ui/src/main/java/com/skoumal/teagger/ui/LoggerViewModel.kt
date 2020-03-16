package com.skoumal.teagger.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import com.skoumal.teagger.StreamLogger
import com.skoumal.teagger.i
import com.skoumal.teagger.shareLog
import com.skoumal.teanity.databinding.GenericRvItem
import com.skoumal.teanity.extensions.bindingOf
import com.skoumal.teanity.extensions.compareToSafe
import com.skoumal.teanity.viewevent.base.ActivityExecutor
import com.skoumal.teanity.viewevent.base.ContextExecutor
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @param streamLogger The [StreamLogger] which has been used for logging
 * @param authority The [androidx.core.content.FileProvider] authority which has access to the teagger/ directory in cache
 */
class LoggerViewModel(
    private val streamLogger: StreamLogger,
    private val authority: String
) : TeanityViewModel() {

    val binding = bindingOf<LogLineItem> { }
    val items = ObservableArrayList<LogLineItem>()

    private val refreshJob: Job

    init {
        refreshJob = launch {
            val presentLines = streamLogger.collect().readLines().map { LogLineItem(it) }
            items.addAll(presentLines.asReversed())
            streamLogger.observe().collect {
                items.add(0, LogLineItem(it))
            }
        }
    }

    override fun onCleared() {
        refreshJob.cancel()
        super.onCleared()
    }

    fun sendLog() {
        SendLogEvent(streamLogger, authority, this).publish()
    }

    fun wipeLog() {
        launch {
            streamLogger.clear()
            FinishActivityEvent.publish()
        }
    }

    fun sampleLog() {
        streamLogger.i("Test", "${System.currentTimeMillis()}", null)
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

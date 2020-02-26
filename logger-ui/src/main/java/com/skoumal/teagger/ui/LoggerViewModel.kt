package com.skoumal.teagger.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import com.skoumal.teagger.StreamLogger
import com.skoumal.teanity.databinding.GenericRvItem
import com.skoumal.teanity.extensions.bindingOf
import com.skoumal.teanity.extensions.compareToSafe
import com.skoumal.teanity.viewevent.base.ActivityExecutor
import com.skoumal.teanity.viewevent.base.ContextExecutor
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel
import kotlinx.coroutines.launch

class LoggerViewModel(private val streamLogger: StreamLogger) : TeanityViewModel() {

    val binding = bindingOf<LogLineItem> { }
    val items = ObservableArrayList<LogLineItem>()

    init {
        launch {
            streamLogger.getLogAsString().split('\n').forEach {
                items += LogLineItem(it)
            }
        }
    }

    fun sendLog() {
        SendLogEvent(streamLogger).publish()
    }

    fun wipeLog() {
        streamLogger.wipeLog()
        FinishActivityEvent.publish()
    }

    class SendLogEvent(private val streamLogger: StreamLogger) : ViewEvent(), ContextExecutor {
        override fun invoke(context: Context) {
            streamLogger.shareLog(context, "com.teagger.fileprovider")
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

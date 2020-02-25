package com.skoumal.teagger

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import com.skoumal.teanity.databinding.GenericRvItem
import com.skoumal.teanity.extensions.bindingOf
import com.skoumal.teanity.extensions.compareToSafe
import com.skoumal.teanity.viewevent.base.ActivityExecutor
import com.skoumal.teanity.viewevent.base.ContextExecutor
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel
import kotlinx.coroutines.launch

class LoggerViewModel(private val fileLogger: FileLogger) : TeanityViewModel() {

    val binding = bindingOf<LogLineItem> { }
    val items = ObservableArrayList<LogLineItem>()

    init {
        launch {
            fileLogger.getLogAsString().split('\n').forEach {
                items += LogLineItem(it)
            }
        }
    }

    fun sendLog() {
        fileLogger.file?.let {
            SendLogEvent(fileLogger).publish()
        }
    }

    fun wipeLog() {
        fileLogger.wipeLog()
        FinishActivityEvent.publish()
    }

    class SendLogEvent(private val fileLogger: FileLogger) : ViewEvent(), ContextExecutor {
        override fun invoke(context: Context) {
            fileLogger.shareLog(context)
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

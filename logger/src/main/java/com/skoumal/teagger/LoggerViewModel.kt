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

class LoggerViewModel : TeanityViewModel() {

    val binding = bindingOf<LogLineItem> { }
    val items = ObservableArrayList<LogLineItem>()

    init {
        launch {
            FileLogger.getLogAsString().split('\n').forEach {
                items += LogLineItem(it)
            }
        }
    }

    fun sendLog() {
        FileLogger.file?.let {
            SendLogEvent().publish()
        }
    }

    fun wipeLog() {
        FileLogger.wipeLog()
        FinishActivityEvent.publish()
    }

    class SendLogEvent : ViewEvent(), ContextExecutor {
        override fun invoke(context: Context) {
            FileLogger.shareLog(context)
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

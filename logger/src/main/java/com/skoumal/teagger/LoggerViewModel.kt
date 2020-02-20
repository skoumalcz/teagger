package com.skoumal.teagger

import androidx.databinding.ObservableArrayList
import com.skoumal.teanity.databinding.GenericRvItem
import com.skoumal.teanity.extensions.bindingOf
import com.skoumal.teanity.util.KObservableField
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel
import java.io.File

class LoggerViewModel : TeanityViewModel() {

    val binding = bindingOf<LogLineItem> { }

    var text = KObservableField(FileLogger.getLogAsString())

    val items = ObservableArrayList<LogLineItem>()

    init {
        text.value.split('\n').forEach {
            items += LogLineItem(it)
        }
    }

    fun sendLog() {
        SendLogEvent(FileLogger.file!!).publish()
    }

    fun wipeLog() {
        FileLogger.wipeLog()
        FinishActivityEvent.publish()
    }

    class SendLogEvent(val file: File) : ViewEvent()
    object FinishActivityEvent : ViewEvent()

    class LogLineItem(val text: String) : GenericRvItem() {

        override val layoutRes = R.layout.item_log_line

        override fun contentSameAs(other: GenericRvItem) = false
        override fun sameAs(other: GenericRvItem) = false
    }
}

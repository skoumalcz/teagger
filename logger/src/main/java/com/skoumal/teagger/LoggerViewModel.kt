package com.skoumal.teagger

import com.skoumal.teanity.util.KObservableField
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel

class LoggerViewModel : TeanityViewModel() {

    var text = KObservableField(FileLogger.getLogAsString())

    fun sendLog() {
        SendLogEvent(text.value).publish()
    }

    fun wipeLog() {
        FileLogger.wipeLog()
        FinishActivityEvent.publish()
    }

    class SendLogEvent(val text: String) : ViewEvent()
    object FinishActivityEvent : ViewEvent()
}

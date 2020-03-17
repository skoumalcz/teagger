package com.skoumal.teagger.ui

import androidx.appcompat.app.AppCompatActivity
import com.skoumal.teagger.Teagger
import com.skoumal.teagger.i
import com.skoumal.teanity.viewevent.base.ActivityExecutor
import com.skoumal.teanity.viewevent.base.ViewEvent
import com.skoumal.teanity.viewmodel.TeanityViewModel

class LoggerViewModel : TeanityViewModel(), LoggerDataProvider by LoggerDataProvider.impl {

    override fun onCleared() {
        disposeDataProvider()
        super.onCleared()
    }

    fun wipeLog() {
        clearLog()
        FinishActivityEvent.publish()
    }

    fun sampleLog() {
        Teagger.instance.i("Test", "${System.currentTimeMillis()}", null)
    }

    object FinishActivityEvent : ViewEvent(), ActivityExecutor {
        override fun invoke(activity: AppCompatActivity) {
            activity.finish()
        }
    }

}

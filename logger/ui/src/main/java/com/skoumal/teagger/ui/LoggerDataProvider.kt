package com.skoumal.teagger.ui

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.skoumal.teagger.Teagger
import com.skoumal.teagger.shareLog
import com.skoumal.teanity.extensions.bindingOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import me.tatarka.bindingcollectionadapter2.OnItemBind

interface LoggerDataProvider {

    val items: ObservableList<LoggerLine>
    val itemBinding: OnItemBind<LoggerLine>

    fun disposeDataProvider()

    fun shareLog(context: Context)
    fun clearLog()

    companion object {
        val impl: LoggerDataProvider get() = LoggerDataProviderImpl()
    }

}

internal class LoggerDataProviderImpl :
    LoggerDataProvider,
    CoroutineScope by MainScope() {

    override val items = ObservableArrayList<LoggerLine>()
    override val itemBinding = bindingOf<LoggerLine> {}

    private val refreshJob: Job

    init {
        refreshJob = launch {
            val presentLines = withContext(Dispatchers.IO) {
                Teagger.instance.collect().readLines().map { LoggerLine(it) }
            }
            items.addAll(presentLines.asReversed())
            Teagger.instance.observe().collect {
                items.add(0, LoggerLine(it))
            }
        }
    }

    override fun disposeDataProvider() {
        refreshJob.cancel()
    }

    override fun shareLog(context: Context) {
        launch {
            Teagger.instance.shareLog(context, R.string.teagger_log_share_authority)
        }
    }

    override fun clearLog() {
        launch {
            Teagger.instance.clear()
        }
    }
}
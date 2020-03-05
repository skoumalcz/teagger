package com.skoumal.teagger

class StreamLoggerAsyncTest : StreamLoggerTest() {
    override fun createLogger(
            outputProvider: OutputStreamProvider,
            inputProvider: InputStreamProvider,
            clearCallback: () -> Unit
    ) = StreamLoggerAsync(outputProvider, inputProvider, clearCallback)

    override fun getLogEntryDelegate(streamLogger: StreamLogger) =
            streamLogger as StreamLoggerAsync
}

package com.skoumal.teagger

class StreamLoggerSyncTest : StreamLoggerTest() {
    override fun createLogger(
            outputProvider: OutputStreamProvider,
            inputProvider: InputStreamProvider,
            clearCallback: () -> Unit
    ) = StreamLoggerSync(outputProvider, inputProvider, clearCallback)

    override fun getLogEntryDelegate(streamLogger: StreamLogger) =
            streamLogger as StreamLoggerSync
}

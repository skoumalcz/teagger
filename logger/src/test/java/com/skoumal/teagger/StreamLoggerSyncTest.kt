package com.skoumal.teagger

import com.skoumal.teagger.base.StreamLoggerBaseTest
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider

class StreamLoggerSyncTest : StreamLoggerBaseTest() {
    override fun createLogger(
        outputProvider: OutputStreamProvider,
        inputProvider: InputStreamProvider,
        clearCallback: () -> Unit
    ) = StreamLoggerSync(outputProvider, inputProvider, clearCallback)

    override fun getLogEntryDelegate(streamLogger: StreamLogger) =
            streamLogger as StreamLoggerSync

    override fun testLogMethod(
            logger: StreamLogger,
            logBlock: () -> Unit,
            assertBlock: () -> Unit
    ) {
        logBlock()
        assertBlock()
    }
}

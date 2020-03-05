package com.skoumal.teagger

import com.skoumal.teagger.base.StreamLoggerBaseTest
import kotlinx.coroutines.runBlocking

class StreamLoggerAsyncTest : StreamLoggerBaseTest() {
    override fun createLogger(
            outputProvider: OutputStreamProvider,
            inputProvider: InputStreamProvider,
            clearCallback: () -> Unit
    ) = StreamLoggerAsync(outputProvider, inputProvider, clearCallback)

    override fun getLogEntryDelegate(streamLogger: StreamLogger) =
            streamLogger as StreamLoggerAsync

    override fun testLogMethod(
            logger: StreamLogger,
            logBlock: () -> Unit,
            assertBlock: () -> Unit
    ) {
        if (logger !is StreamLoggerAsync)
            throw IllegalArgumentException()

        logBlock()
        runBlocking(logger.singleThreadContext) {
            assertBlock()
        }
    }
}

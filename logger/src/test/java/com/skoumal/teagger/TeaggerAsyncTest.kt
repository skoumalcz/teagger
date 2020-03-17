package com.skoumal.teagger

import com.skoumal.teagger.base.TeaggerBaseTest
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import kotlinx.coroutines.runBlocking

class TeaggerAsyncTest : TeaggerBaseTest() {
    override fun createLogger(
        outputProvider: OutputStreamProvider,
        inputProvider: InputStreamProvider,
        clearCallback: () -> Unit
    ) = StreamLoggerAsync(outputProvider, inputProvider, clearCallback)

    override fun getLogEntryDelegate(teagger: Teagger) =
        teagger as StreamLoggerAsync

    override fun testLogMethod(
        logger: Teagger,
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

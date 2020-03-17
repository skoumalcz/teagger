package com.skoumal.teagger

import com.skoumal.teagger.base.TeaggerBaseTest
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider

class TeaggerSyncTest : TeaggerBaseTest() {
    override fun createLogger(
        outputProvider: OutputStreamProvider,
        inputProvider: InputStreamProvider,
        clearCallback: () -> Unit
    ) = StreamLoggerSync(outputProvider, inputProvider, clearCallback)

    override fun getLogEntryDelegate(teagger: Teagger) =
        teagger as StreamLoggerSync

    override fun testLogMethod(
        logger: Teagger,
        logBlock: () -> Unit,
        assertBlock: () -> Unit
    ) {
        logBlock()
        assertBlock()
    }
}

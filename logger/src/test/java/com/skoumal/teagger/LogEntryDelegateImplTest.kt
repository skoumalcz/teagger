package com.skoumal.teagger

import android.util.Log
import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teagger.entry.LogEntryDelegateImpl
import org.junit.Before
import org.junit.Test

class LogEntryDelegateImplTest {

    private lateinit var logEntryDelegate: LogEntryDelegate

    @Before
    fun init() {
        logEntryDelegate = LogEntryDelegateImpl()
    }

    @Test
    fun entryFor_containsAllParams() {
        val tag = "Test"
        val message = "Test message"

        var entry = logEntryDelegate.entryFor(Log.DEBUG, tag, message)
        assert(entry.startsWith("D"))
        assert(entry.contains(tag))
        assert(entry.contains(message))

        entry = logEntryDelegate.entryFor(Log.ASSERT, tag, message)
        assert(entry.startsWith("A"))
        assert(entry.contains(tag))
        assert(entry.contains(message))
    }

}

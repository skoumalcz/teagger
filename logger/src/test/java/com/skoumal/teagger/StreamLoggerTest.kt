package com.skoumal.teagger

import android.util.Log
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.RuntimeException

class StreamLoggerTest {

    private lateinit var logger: StreamLogger
    private lateinit var loggedContent: String

    private lateinit var outputStreamProvider: OutputStreamProvider
    private lateinit var inputStreamProvider: InputStreamProvider
    private val clearCallback = {
        loggedContent = ""
    }

    @Before
    fun prepare() {
        loggedContent = ""

        outputStreamProvider = object : OutputStreamProvider {
            override fun provideOutputStream() = object : ByteArrayOutputStream() {
                override fun close() {
                    super.close()
                    loggedContent += this.toString()
                }
            }

        }

        inputStreamProvider = object : InputStreamProvider {
            override fun provideInputStream() = loggedContent.byteInputStream()
        }

        logger = StreamLogger(outputStreamProvider, inputStreamProvider, clearCallback)
    }

    @Test
    fun log_appendsEntries() {
        val tag = "Test"
        var message = "Test message"

        val entry1 = logger.entryFor(Log.DEBUG, tag, message)
        logger.log(Log.DEBUG, tag, message, null)

        message = "Test message 2"
        val entry2 = logger.entryFor(Log.WARN, tag, message)
        val throwable = getThrowableWithStackTrace()
        val exceptionName = throwable::class.java.name
        logger.log(Log.WARN, tag, message, throwable)

        assert(loggedContent.startsWith(entry1))
        assert(loggedContent.substringAfter(entry1).trim().startsWith(entry2))
        assert(loggedContent.substringAfter(entry2).trim().startsWith(exceptionName))
    }

    @Test
    fun logFunctions_appendCorrectEntries() {
        val tag = "Tag"

        var message = "debug message"
        val entryDebug = logger.entryFor(Log.DEBUG, tag, message)
        logger.d(tag, message, null)

        message = "info message"
        val entryInfo = logger.entryFor(Log.INFO, tag, message)
        logger.i(tag, message, null)

        message = "verbose message"
        val entryVerbose = logger.entryFor(Log.VERBOSE, tag, message)
        logger.v(tag, message, null)

        message = "warn message"
        val entryWarn = logger.entryFor(Log.WARN, tag, message)
        logger.w(tag, message, null)

        message = "error message"
        val entryError = logger.entryFor(Log.ERROR, tag, message)
        logger.e(tag, message, null)

        message = "assert message"
        val entryAssert = logger.entryFor(Log.ASSERT, tag, message)
        logger.wtf(tag, message, null)

        assert(loggedContent.startsWith(entryDebug))
        assert(loggedContent.substringAfter(entryDebug).trim().startsWith(entryInfo))
        assert(loggedContent.substringAfter(entryInfo).trim().startsWith(entryVerbose))
        assert(loggedContent.substringAfter(entryVerbose).trim().startsWith(entryWarn))
        assert(loggedContent.substringAfter(entryWarn).trim().startsWith(entryError))
        assert(loggedContent.substringAfter(entryError).trim().startsWith(entryAssert))
    }

    @Test
    fun entryFor_containsAllParams() {
        val tag = "Test"
        val message = "Test message"

        var entry = logger.entryFor(Log.DEBUG, tag, message)
        assert(entry.startsWith("D"))
        assert(entry.contains(tag))
        assert(entry.contains(message))

        entry = logger.entryFor(Log.ASSERT, tag, message)
        assert(entry.startsWith("A"))
        assert(entry.contains(tag))
        assert(entry.contains(message))
    }

    @Test
    fun wipeLog_callsCallback() {
        loggedContent = "content to be wiped"
        logger.wipeLog()
        assertEquals("", loggedContent)
    }

    @Test
    fun getLogAsString_returnsFullLog() {
        loggedContent = "logged content"
        assertEquals(loggedContent, logger.getLogAsString())
    }

    private fun getThrowableWithStackTrace(): Throwable {
        try {
            1 / 0
        } catch (t: Throwable) {
            return t
        }
        return null!!
    }
}

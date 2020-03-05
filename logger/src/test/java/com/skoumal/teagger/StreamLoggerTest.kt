package com.skoumal.teagger

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

abstract class StreamLoggerTest {

    private lateinit var logger: StreamLogger
    private lateinit var logEntryDelegate: LogEntryDelegate
    private lateinit var loggedContent: String

    private lateinit var outputStreamProvider: OutputStreamProvider
    private lateinit var inputStreamProvider: InputStreamProvider
    private val clearCallback = {
        loggedContent = ""
    }

    abstract fun createLogger(
            outputProvider: OutputStreamProvider,
            inputProvider: InputStreamProvider,
            clearCallback: () -> Unit
    ): StreamLogger

    abstract fun getLogEntryDelegate(streamLogger: StreamLogger): LogEntryDelegate

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

        logger = createLogger(outputStreamProvider, inputStreamProvider, clearCallback)
        logEntryDelegate = getLogEntryDelegate(logger)
    }

    @Test
    fun log_appendsEntries() {
        val tag = "Test"
        var message = "Test message"

        val entry1 = logEntryDelegate.entryFor(Log.DEBUG, tag, message)
        logger.log(Log.DEBUG, tag, message, null)

        message = "Test message 2"
        val entry2 = logEntryDelegate.entryFor(Log.WARN, tag, message)
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
        val entryDebug = logEntryDelegate.entryFor(Log.DEBUG, tag, message)
        logger.d(tag, message, null)

        message = "info message"
        val entryInfo = logEntryDelegate.entryFor(Log.INFO, tag, message)
        logger.i(tag, message, null)

        message = "verbose message"
        val entryVerbose = logEntryDelegate.entryFor(Log.VERBOSE, tag, message)
        logger.v(tag, message, null)

        message = "warn message"
        val entryWarn = logEntryDelegate.entryFor(Log.WARN, tag, message)
        logger.w(tag, message, null)

        message = "error message"
        val entryError = logEntryDelegate.entryFor(Log.ERROR, tag, message)
        logger.e(tag, message, null)

        message = "assert message"
        val entryAssert = logEntryDelegate.entryFor(Log.ASSERT, tag, message)
        logger.wtf(tag, message, null)

        assert(loggedContent.startsWith(entryDebug))
        assert(loggedContent.substringAfter(entryDebug).trim().startsWith(entryInfo))
        assert(loggedContent.substringAfter(entryInfo).trim().startsWith(entryVerbose))
        assert(loggedContent.substringAfter(entryVerbose).trim().startsWith(entryWarn))
        assert(loggedContent.substringAfter(entryWarn).trim().startsWith(entryError))
        assert(loggedContent.substringAfter(entryError).trim().startsWith(entryAssert))
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
        val result = runBlocking {
            logger.getLogAsString()
        }
        assertEquals(loggedContent, result)
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

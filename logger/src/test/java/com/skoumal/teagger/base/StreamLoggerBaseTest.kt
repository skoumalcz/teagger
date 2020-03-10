package com.skoumal.teagger.base

import android.util.Log
import com.skoumal.teagger.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

abstract class StreamLoggerBaseTest {

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

    @ExperimentalCoroutinesApi
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

        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    abstract fun testLogMethod(logger: StreamLogger, logBlock: () -> Unit, assertBlock: () -> Unit)

    @Test
    fun log_appendsEntries() {
        val tag = "Test"
        val message1 = "Test message"
        val entry1 = logEntryDelegate.entryFor(Log.DEBUG, tag, message1)

        val message2 = "Test message 2"
        val entry2 = logEntryDelegate.entryFor(Log.WARN, tag, message2)
        val throwable = getThrowableWithStackTrace()
        val exceptionName = throwable::class.java.name

        val logBlock = {
            logger.log(Log.DEBUG, tag, message1, null)
            logger.log(Log.WARN, tag, message2, throwable)
        }

        testLogMethod(logger, logBlock) {
            assert(loggedContent.startsWith(entry1))
            assert(loggedContent.substringAfter(entry1).trim().startsWith(entry2))
            assert(loggedContent.substringAfter(entry2).trim().startsWith(exceptionName))
        }
    }

    @Test
    fun logFunctions_appendCorrectEntries() {
        val tag = "Tag"

        val messageDebug = "debug message"
        val entryDebug = logEntryDelegate.entryFor(Log.DEBUG, tag, messageDebug)

        val messageInfo = "info message"
        val entryInfo = logEntryDelegate.entryFor(Log.INFO, tag, messageInfo)

        val messageVerbose = "verbose message"
        val entryVerbose = logEntryDelegate.entryFor(Log.VERBOSE, tag, messageVerbose)

        val messageWarn = "warn message"
        val entryWarn = logEntryDelegate.entryFor(Log.WARN, tag, messageWarn)

        val messageError = "error message"
        val entryError = logEntryDelegate.entryFor(Log.ERROR, tag, messageError)

        val messageAssert = "assert message"
        val entryAssert = logEntryDelegate.entryFor(Log.ASSERT, tag, messageAssert)

        val logBlock = {
            logger.d(tag, messageDebug, null)
            logger.i(tag, messageInfo, null)
            logger.v(tag, messageVerbose, null)
            logger.w(tag, messageWarn, null)
            logger.e(tag, messageError, null)
            logger.wtf(tag, messageAssert, null)
        }

        testLogMethod(logger, logBlock) {
            assert(loggedContent.startsWith(entryDebug))
            assert(loggedContent.substringAfter(entryDebug).trim().startsWith(entryInfo))
            assert(loggedContent.substringAfter(entryInfo).trim().startsWith(entryVerbose))
            assert(loggedContent.substringAfter(entryVerbose).trim().startsWith(entryWarn))
            assert(loggedContent.substringAfter(entryWarn).trim().startsWith(entryError))
            assert(loggedContent.substringAfter(entryError).trim().startsWith(entryAssert))
        }
    }

    @Test
    fun wipeLog_callsCallback() {
        loggedContent = "content to be wiped"
        testLogMethod(logger, {
            logger.wipeLog()
        }, {
            assertEquals("", loggedContent)
        })
    }

    @Test
    fun getLogAsString_returnsFullLog() {
        loggedContent = "logged content"
        val result = runBlocking {
            logger.getLogAsString()
        }
        assertEquals(loggedContent, result)
    }
}

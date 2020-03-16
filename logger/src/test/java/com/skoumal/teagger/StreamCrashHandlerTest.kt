package com.skoumal.teagger

import com.skoumal.teagger.base.getThrowableWithStackTrace
import com.skoumal.teagger.crash.StreamCrashHandler
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class StreamCrashHandlerTest {

    private lateinit var crashHandler: StreamCrashHandler
    private lateinit var primaryLogger: StreamLoggerAsync
    private lateinit var fileLogger: StreamLoggerAsync

    private lateinit var primaryLoggedContent: String
    private lateinit var fileLoggedContent: String

    private lateinit var primaryOutputStreamProvider: OutputStreamProvider
    private lateinit var primaryInputStreamProvider: InputStreamProvider
    private val primaryClearCallback = {
        primaryLoggedContent = ""
    }

    private lateinit var fileOutputStreamProvider: OutputStreamProvider
    private lateinit var fileInputStreamProvider: InputStreamProvider
    private val fileClearCallback = {
        fileLoggedContent = ""
    }

    @Before
    fun init() {
        primaryLoggedContent = ""
        fileLoggedContent = ""

        primaryOutputStreamProvider = object :
            OutputStreamProvider {
            override fun provideOutputStream() = object : ByteArrayOutputStream() {
                override fun close() {
                    super.close()
                    primaryLoggedContent += this.toString()
                }
            }

        }

        primaryInputStreamProvider = object :
            InputStreamProvider {
            override fun provideInputStream() = primaryLoggedContent.byteInputStream()
        }

        primaryLogger = StreamLoggerAsync(
                primaryOutputStreamProvider,
                primaryInputStreamProvider,
                primaryClearCallback
        )

        fileOutputStreamProvider = object :
            OutputStreamProvider {
            override fun provideOutputStream() = object : ByteArrayOutputStream() {
                override fun close() {
                    super.close()
                    fileLoggedContent += this.toString()
                }
            }

        }

        fileInputStreamProvider = object :
            InputStreamProvider {
            override fun provideInputStream() = fileLoggedContent.byteInputStream()
        }

        fileLogger = StreamLoggerAsync(
                fileOutputStreamProvider,
                fileInputStreamProvider,
                fileClearCallback
        )

        crashHandler = StreamCrashHandler(
            primaryLogger,
            fileLogger
        )
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Test
    fun handleCrash_logsToPrimary() {
        val throwable = getThrowableWithStackTrace()
        val exceptionName = throwable::class.java.name
        val entry = primaryLogger.entryFor(
                Constants.CRASH_DEFAULT_PRIORITY,
                Constants.CRASH_DEFAULT_TAG,
                Constants.CRASH_DEFAULT_MESSAGE
        )

        crashHandler.handleCrash(throwable)

        assert(primaryLoggedContent.startsWith(entry))
        assert(primaryLoggedContent.contains(exceptionName))
        assertEquals("", fileLoggedContent)
    }

    @Test
    fun handleCrash_logsToFileOnError() {
        primaryLogger.outputStreamProvider = object :
            OutputStreamProvider {
            override fun provideOutputStream(): OutputStream? {
                throw IllegalArgumentException()
            }
        }

        val throwable = getThrowableWithStackTrace()
        val exceptionName = throwable::class.java.name
        val entry = primaryLogger.entryFor(
                Constants.CRASH_DEFAULT_PRIORITY,
                Constants.CRASH_DEFAULT_TAG,
                Constants.CRASH_DEFAULT_MESSAGE
        )

        crashHandler.handleCrash(throwable)

        assert(fileLoggedContent.startsWith(entry))
        assert(fileLoggedContent.contains(exceptionName))
        assertEquals("", primaryLoggedContent)
    }

    @Test
    fun pushToLog_copiesDataAndClearsFile() {
        val content = "Content that failed to log into primary output stream"
        fileLoggedContent = content
        crashHandler.pushToLog()
        runBlocking(fileLogger.singleThreadContext) {
            // there's no other way around to wait for the previous function to finish,
            // as it also runs some code in the IO dispatcher
            delay(100)
            assertEquals("", fileLoggedContent)
            assertEquals(content, primaryLoggedContent.trim())
        }
    }
}

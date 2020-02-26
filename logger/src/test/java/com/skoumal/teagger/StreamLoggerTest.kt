package com.skoumal.teagger

import android.content.Context
import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File

class StreamLoggerTest {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var filesDir: File

    @Mock
    lateinit var logDir: File

    @Mock
    lateinit var logFile: File

    @Mock
    lateinit var fileThrowing: File

    @Before
    fun prepare() {
        context = Mockito.mock(Context::class.java)
        filesDir = Mockito.mock(File::class.java)
        logDir = Mockito.mock(File::class.java)
        logFile = Mockito.mock(File::class.java)
        fileThrowing = Mockito.mock(File::class.java) {
            throw SecurityException()
        }

        StreamLogger.filesDir = null
        StreamLogger.file = null
        //FileLogger.authority = null

        Mockito.`when`(context.filesDir).thenReturn(filesDir)
        Mockito.`when`(logDir.mkdirs()).then {
            Mockito.`when`(logDir.isDirectory).thenReturn(true)
            true
        }

        Mockito.`when`(logFile.createNewFile()).then {
            Mockito.`when`(logFile.isFile).thenReturn(true)
            true
        }
    }

    @Test
    fun init() {
        val authority = "com.skoumal.authority"
        StreamLogger.init(context, authority)

        assertEquals(filesDir, StreamLogger.filesDir)
        assertEquals(authority, StreamLogger.authority)
    }

    @Test
    fun createLogDir_callsMkdirs() {
        val dir = StreamLogger.run {
            logDir.createLogDir()
        }

        assert(logDir.isDirectory)
        assertEquals(logDir, dir)
    }

    @Test
    fun createLogDir_swallowsExceptions() {
        val dir = StreamLogger.run {
            fileThrowing.createLogDir()
        }

        assertNull(dir)
    }

    @Test
    fun createLogFile_assignsCreatedFile() {
        val fileResult = StreamLogger.run {
            logFile.createLog()
        }

        assertEquals(logFile, fileResult)
        assertEquals(logFile, StreamLogger.file)
        assert(logFile.isFile)
    }

    @Test
    fun createLogFile_swallowsExceptions() {
        val file = StreamLogger.run {
            fileThrowing.createLog()
        }

        assertNull(file)
    }

    @Test
    fun entryFor_containsAllParams() {
        val tag = "Test"
        val message = "Test message"

        var entry = StreamLogger.entryFor(Log.DEBUG, tag, message, null)
        assert(entry.startsWith("D"))
        assert(entry.contains(tag))
        assert(entry.contains(message))

        val throwable = getThrowableWithStackTrace()

        entry = StreamLogger.entryFor(Log.ERROR, tag, null, throwable)
        assert(entry.startsWith("E"))
        assert(entry.contains(tag))
        assert(entry.contains(StreamLogger.run { throwable!!.getStackTraceString() }))
    }

    @Test
    fun getStackTraceString_containsKnownClassNames() {
        val throwable = getThrowableWithStackTrace()
        val stackTrace = StreamLogger.run {
            throwable?.getStackTraceString() ?: ""
        }

        assert(stackTrace.contains(throwable!!::class.java.simpleName))
        assert(stackTrace.contains(this::class.java.simpleName))
    }

    @Test
    fun wipeLog_swallowsExceptions() {
        // FileLogger.file is now null
        StreamLogger.wipeLog()

        StreamLogger.file = fileThrowing
        StreamLogger.wipeLog()
    }

    @Test
    fun getLogAsString_EmptyStringOnFileNull() {
        assertEquals("", StreamLogger.getLogAsString())
    }

    private fun getThrowableWithStackTrace(): Throwable? {
        try {
            1 / 0
        } catch (t: Throwable) {
            return t
        }
        return null
    }
}

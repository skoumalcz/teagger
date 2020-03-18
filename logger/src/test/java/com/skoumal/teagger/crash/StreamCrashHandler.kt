package com.skoumal.teagger.crash

import com.skoumal.teagger.Teagger
import com.skoumal.teagger.helper.provideTeagger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Test
import kotlin.concurrent.thread

class StreamCrashHandlerTest : CoroutineScope by TestCoroutineScope() {

    private lateinit var teagger: Teagger

    @Before
    fun prepare() {
        teagger = provideTeagger()
    }

    @Test
    fun test_threadCrash() {
        // this is just so we know that crash handler doesn't invoke additional crashes
        thread {
            throw IllegalArgumentException()
        }.join()
    }

}
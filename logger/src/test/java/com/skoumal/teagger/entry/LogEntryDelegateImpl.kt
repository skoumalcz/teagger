package com.skoumal.teagger.entry

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.skoumal.teagger.Constants
import org.junit.Before
import org.junit.Test
import kotlin.random.Random.Default.nextBytes
import kotlin.random.Random.Default.nextInt

class LogEntryDelegateImplTest {

    private lateinit var entry: LogEntryDelegate

    @Before
    fun init() {
        entry = LogEntryDelegate.default
    }

    @Test
    fun test_hasParams() {
        val priority = priority
        val tag = text
        val message = text

        entry.entryFor(priority, tag, message).also {
            assertThat(it).startsWith(LogEntryDelegateImpl.priorities[priority])
            assertThat(it).contains(tag)
            assertThat(it).containsMatch("[(][0-9]*[)]")
            assertThat(it).contains(message)
        }
    }

    @Test
    fun test_hasParams_throwable() {
        entry.entryFor(IllegalStateException()).also {
            assertThat(it).startsWith(LogEntryDelegateImpl.priorities[Log.ERROR])
            assertThat(it).contains(Constants.CRASH_DEFAULT_MESSAGE)
            assertThat(it).containsMatch("[(][0-9]*[)]")
        }
    }

    @Test
    fun test_hasParams_default() {
        entry.entryFor(-10, text, text).also {
            assertThat(it).startsWith(LogEntryDelegateImpl.priorityName)
        }
    }

    @Test
    fun test_blankMessage() {
        val priority = priority
        val tag = text

        entry.entryFor(priority, tag, "").also {
            assertThat(it).containsMatch("[{].*[}]")
        }
    }

    @Test
    fun test_resolvePriority() {
        val priority = priority

        entry.entryFor(priority, text, text).also {
            entry.resolvePriorityForEntry(it).also {
                assertThat(it).isEqualTo(priority)
            }
        }
    }

    @Test
    fun test_resolvePriority_undefined() {
        val line = "same random message"
        entry.resolvePriorityForEntry(line).also {
            assertThat(it).isNotIn(LogEntryDelegateImpl.priorities.keys)
        }
    }

    @Test
    fun test_resolvePriority_throwable() {
        val line = " ".repeat(nextInt(1, 20)) + "at Something.something(Something.java:10)"
        entry.resolvePriorityForEntry(line).also {
            assertThat(it).isEqualTo(Log.ERROR)
        }
    }

    // ---

    private val priority get() = LogEntryDelegateImpl.priorities.keys.random()
    private val text get() = String(nextBytes(nextInt(10, 20)))

}
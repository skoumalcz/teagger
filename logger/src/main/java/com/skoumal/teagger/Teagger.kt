package com.skoumal.teagger

import android.content.Context
import android.net.Uri
import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teagger.provider.CleanupProvider
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import com.skoumal.teagger.provider.file.FileProvider
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.lang.ref.WeakReference


interface Teagger {

    var entryTransformer: LogEntryDelegate

    fun log(priority: Int, tag: String, message: String)
    fun log(throwable: Throwable)

    suspend fun collect(): File
    suspend fun collect(context: Context, authority: Int): Uri
    suspend fun collect(context: Context, authority: String): Uri
    suspend fun observe(): Flow<String>
    suspend fun clear(): Boolean

    companion object {

        private var _context: WeakReference<Context>? = null
        internal var context
            get() = _context?.get()!!
            set(value) {
                _context?.clear()
                _context = WeakReference(value.applicationContext)
            }

        val isInitialized get() = this::instance.isInitialized

        lateinit var instance: Teagger
            private set

        fun start(context: Context, body: Initializer.() -> Unit): Teagger {
            this.context = context
            return start(body)
        }

        // This should remain invisible to users as it skips setting context for TEST ONLY!
        internal inline fun start(body: Initializer.() -> Unit): Teagger {
            val init = Initializer().also(body)
            if (!isInitialized) {
                init.asDefault()
            }
            return instance
        }

    }

    class Initializer internal constructor() {

        internal fun asDefault(): Teagger {
            return TeaggerImpl(FileProvider()).also {
                instance = it
            }
        }

        fun withFile(file: File): Teagger {
            return TeaggerImpl(FileProvider(file)).also {
                instance = it
            }
        }

        fun withProviders(
            inputStream: InputStreamProvider,
            outputStream: OutputStreamProvider,
            cleanup: CleanupProvider? = null
        ): Teagger {
            return TeaggerImpl(inputStream, outputStream, cleanup).also {
                instance = it
            }
        }
    }

}

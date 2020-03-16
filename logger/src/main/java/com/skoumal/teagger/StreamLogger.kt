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


interface StreamLogger {

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
        internal val context get() = _context?.get()!!

        @JvmStatic
        @JvmName("withFile")
        operator fun invoke(context: Context, file: File): StreamLogger {
            _context = WeakReference(context.applicationContext)
            return StreamLoggerImpl(FileProvider(file))
        }

        @JvmStatic
        @JvmName("withContext")
        operator fun invoke(context: Context): StreamLogger {
            _context = WeakReference(context.applicationContext)
            return StreamLoggerImpl(FileProvider())
        }

        @JvmStatic
        @JvmName("withProviders")
        operator fun invoke(
            context: Context,
            inputStream: InputStreamProvider,
            outputStream: OutputStreamProvider,
            cleanup: CleanupProvider? = null
        ): StreamLogger {
            _context = WeakReference(context.applicationContext)
            return StreamLoggerImpl(inputStream, outputStream, cleanup)
        }

    }

}

package com.skoumal.teagger

import android.content.Context
import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teagger.provider.file.FileProvider
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.lang.ref.WeakReference


interface StreamLogger {

    var entryTransformer: LogEntryDelegate

    fun log(priority: Int, tag: String, message: String)
    fun log(throwable: Throwable)

    /**
     * Fetching log is virtually impossible to achieve with anything but IO work, hence it always
     * runs on [Dispatchers.IO].
     * */
    @Deprecated("Will be replaced by collect(Context): File and observe()")
    suspend fun collect(): String
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

    }

}

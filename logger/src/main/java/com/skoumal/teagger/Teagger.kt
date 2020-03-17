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
        internal val context get() = _context?.get()!!

        lateinit var instance: Teagger
            private set

        @JvmStatic
        @JvmName("withFile")
        operator fun invoke(context: Context, file: File): Teagger {
            _context = WeakReference(context.applicationContext)
            return TeaggerImpl(FileProvider(file)).also {
                instance = it
            }
        }

        @JvmStatic
        @JvmName("withContext")
        operator fun invoke(context: Context): Teagger {
            _context = WeakReference(context.applicationContext)
            return TeaggerImpl(FileProvider()).also {
                instance = it
            }
        }

        @JvmStatic
        @JvmName("withProviders")
        operator fun invoke(
            context: Context,
            inputStream: InputStreamProvider,
            outputStream: OutputStreamProvider,
            cleanup: CleanupProvider? = null
        ): Teagger {
            _context = WeakReference(context.applicationContext)
            return TeaggerImpl(inputStream, outputStream, cleanup).also {
                instance = it
            }
        }

    }

}

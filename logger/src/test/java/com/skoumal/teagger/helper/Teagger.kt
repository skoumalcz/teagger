package com.skoumal.teagger.helper

import com.skoumal.teagger.Teagger
import com.skoumal.teagger.provider.CleanupProvider
import com.skoumal.teagger.provider.InputStreamProvider
import com.skoumal.teagger.provider.OutputStreamProvider
import java.io.ByteArrayOutputStream

internal fun provideTeagger(): Teagger {
    var data = ""
    return provideTeagger(input = { data }, output = { data = it }, cleanup = { data = ""; true })
}

internal inline fun provideTeagger(
    crossinline input: LambdaInputStreamProvider,
    crossinline output: LambdaOutputStreamProvider,
    noinline cleanup: LambdaCleanupProvider? = null
) = Teagger.start {
    withProviders(
        provideInputStream(input),
        provideOutputStream(output),
        cleanup?.let { provideCleanup(it) }
    )
}

typealias LambdaOutputStreamProvider = (String) -> Unit

inline fun provideOutputStream(
    crossinline onResult: LambdaOutputStreamProvider
) = object : OutputStreamProvider {
    override fun provideOutputStream() = object : ByteArrayOutputStream() {
        override fun close() {
            super.close()
            onResult(this.toString())
        }
    }
}

typealias LambdaInputStreamProvider = () -> String

inline fun provideInputStream(
    crossinline provideData: LambdaInputStreamProvider
) = object : InputStreamProvider {
    override fun provideInputStream() = provideData().byteInputStream()
}

typealias LambdaCleanupProvider = () -> Boolean

inline fun provideCleanup(
    crossinline cleanup: LambdaCleanupProvider
) = object : CleanupProvider {
    override fun clean(): Boolean = cleanup()
}
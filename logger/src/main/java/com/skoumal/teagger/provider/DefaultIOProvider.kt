package com.skoumal.teagger.provider

import com.skoumal.teagger.Constants
import com.skoumal.teagger.provider.file.FileProvider

private val fileProvider by lazy { FileProvider(Constants.fatalFile) }

internal fun InputStreamProvider.provideOrDefault() = provideOrDefault {
    fileProvider.provideInputStream()
}

internal fun OutputStreamProvider.provideOrDefault() = provideOrDefault {
    fileProvider.provideOutputStream()
}
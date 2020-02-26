package com.skoumal.teagger

import java.io.InputStream

interface InputStreamProvider {
    fun provideInputStream(): InputStream?
}

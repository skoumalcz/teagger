package com.skoumal.teagger

import java.io.OutputStream

interface OutputStreamProvider {
    fun provideOutputStream(): OutputStream?
}

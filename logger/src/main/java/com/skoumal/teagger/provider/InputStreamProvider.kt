package com.skoumal.teagger.provider

import java.io.InputStream

interface InputStreamProvider {

    /**
     * Provides input stream from any underlying source. The provider does maximum to provide
     * stable stream.
     *
     * For example file provider would try to create the file if it doesn't exist.
     * */
    fun provideInputStream(): InputStream

}

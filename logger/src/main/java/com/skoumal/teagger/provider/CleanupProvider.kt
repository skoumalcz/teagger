package com.skoumal.teagger.provider

interface CleanupProvider {

    /**
     * Cleans the output within the parent object, if possible.
     *
     * @return true if the output has been cleared, false otherwise
     * */
    fun clean(): Boolean

}
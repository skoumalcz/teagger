package com.skoumal.teagger.base

fun getThrowableWithStackTrace(): Throwable {
    try {
        1 / 0
    } catch (t: Throwable) {
        return t
    }
    return null!!
}

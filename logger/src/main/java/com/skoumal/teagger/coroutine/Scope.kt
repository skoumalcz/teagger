package com.skoumal.teagger.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Suppress("FunctionName")
fun IOScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
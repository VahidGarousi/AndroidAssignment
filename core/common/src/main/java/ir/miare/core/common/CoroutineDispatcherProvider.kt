package ir.miare.core.common

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatcherProvider {
    fun defaultDispatcher(): CoroutineDispatcher
    fun uiDispatcher(): CoroutineDispatcher
    fun ioDispatcher(): CoroutineDispatcher
    fun immediateDispatcher(): CoroutineDispatcher
}

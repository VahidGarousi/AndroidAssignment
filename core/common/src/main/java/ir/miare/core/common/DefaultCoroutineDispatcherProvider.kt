package ir.miare.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultCoroutineDispatcherProvider @Inject constructor() : CoroutineDispatcherProvider {
    override fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    override fun uiDispatcher(): CoroutineDispatcher = Dispatchers.Main
    override fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
    override fun immediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}

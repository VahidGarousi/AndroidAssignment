package ir.miare.feature.player.presentation.list

import ir.miare.core.common.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class TestDispatcherProvider(
    private val dispatcher: CoroutineDispatcher = StandardTestDispatcher()
) : CoroutineDispatcherProvider {
    override fun defaultDispatcher(): CoroutineDispatcher = dispatcher
    override fun uiDispatcher(): CoroutineDispatcher = dispatcher
    override fun ioDispatcher(): CoroutineDispatcher = dispatcher
    override fun immediateDispatcher(): CoroutineDispatcher = dispatcher
}

package ir.miare.core.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.miare.core.common.CoroutineDispatcherProvider
import ir.miare.core.common.DefaultCoroutineDispatcherProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DispatchersModule {
    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        impl: DefaultCoroutineDispatcherProvider
    ) : CoroutineDispatcherProvider
}
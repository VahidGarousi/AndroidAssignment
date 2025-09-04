package ir.miare.feature.player.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.miare.feature.player.data.repository.PlayerRepositoryImpl
import ir.miare.feature.player.domain.repository.PlayerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerDataBindingModule {
    @Binds
    @Singleton
    abstract fun bindsPlayerRepository(
        impl: PlayerRepositoryImpl,
    ): PlayerRepository
}
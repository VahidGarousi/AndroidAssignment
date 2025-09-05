package ir.miare.feature.player.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.miare.feature.player.data.remote.api.retrofit.PlayerApiService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerDataProvidingModule {
    @Provides
    @Singleton
    fun providesPlayerAPI(
        retrofit: Retrofit,
    ): PlayerApiService = retrofit.create<PlayerApiService>()
}
package ir.miare.core.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.miare.core.network.BuildConfig
import ir.miare.core.network.okhttp.interceptor.AppErrorInterceptor
import ir.miare.core.network.okhttp.interceptor.LocalJsonInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {
    @Provides
    internal fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .apply {
                level =
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
            }
    }

    @Provides
    internal fun provideAppErrorInterceptor(): AppErrorInterceptor = AppErrorInterceptor()

    @Provides
    internal fun provideLocalJsonInterceptor(
        @ApplicationContext context: Context
    ): LocalJsonInterceptor = LocalJsonInterceptor(context = context)

    @Provides
    internal fun provideOkHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
        appErrorInterceptor: AppErrorInterceptor,
        localJsonInterceptor: LocalJsonInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(appErrorInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(localJsonInterceptor)
            .build()
}
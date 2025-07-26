package com.github.cwramirezg.micredito.home.di

import com.github.cwramirezg.micredito.core.data.network.ApiInterceptor
import com.github.cwramirezg.micredito.home.data.remote.CreditoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiInterceptor: ApiInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val networkJson = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.mibanco.com.pe/v1/")
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCreditoApiService(retrofit: Retrofit): CreditoApiService {
        return retrofit.create(CreditoApiService::class.java)
    }
}

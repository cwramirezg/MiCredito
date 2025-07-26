package com.github.cwramirezg.micredito.home.di

import com.github.cwramirezg.micredito.home.data.repository.CreditoRepositoryImpl
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCreditoRepository(
        creditoRepositoryImpl: CreditoRepositoryImpl
    ): CreditoRepository
}

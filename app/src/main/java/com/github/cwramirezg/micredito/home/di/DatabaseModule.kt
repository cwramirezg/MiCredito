package com.github.cwramirezg.micredito.home.di

import android.content.Context
import androidx.room.Room
import com.github.cwramirezg.micredito.home.data.local.dao.CreditoDao
import com.github.cwramirezg.micredito.home.data.local.dao.SimulacionDao
import com.github.cwramirezg.micredito.home.data.local.database.CreditoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCreditoDatabase(
        @ApplicationContext context: Context
    ): CreditoDatabase {
        return Room.databaseBuilder(
            context,
            CreditoDatabase::class.java,
            CreditoDatabase.DATABASE_NAME
        )
            .build()
    }

    @Provides
    fun provideCreditoDao(database: CreditoDatabase): CreditoDao {
        return database.creditoDao()
    }

    @Provides
    fun provideSimulacionDao(database: CreditoDatabase): SimulacionDao {
        return database.simulacionDao()
    }
}

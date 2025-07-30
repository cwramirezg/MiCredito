package com.github.cwramirezg.micredito.home.di

import com.github.cwramirezg.micredito.navigation.utils.ArgsProvider
import com.github.cwramirezg.micredito.navigation.utils.SavedStateArgsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelArgsModule {

    @Binds
    abstract fun bindSimulacionArgsProvider(
        savedStateArgsProvider: SavedStateArgsProvider
    ): ArgsProvider
}
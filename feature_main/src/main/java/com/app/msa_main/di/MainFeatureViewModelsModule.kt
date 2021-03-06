package com.app.msa_main.di

import androidx.lifecycle.ViewModel
import com.app.msa_main.main.MainActivityViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}
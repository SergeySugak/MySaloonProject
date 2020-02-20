package com.app.msa.di

import androidx.lifecycle.ViewModel
import com.app.msa.scopes.MainScope
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import main.java.com.app.msa.main.MainActivityViewModel

@Module
abstract class MainFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    @MainScope
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}
package com.app.msa.di

import androidx.lifecycle.ViewModel
import com.app.msa.scopes.AuthScope
import com.app.msa.ui.AuthActivityViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthActivityViewModel::class)
    @AuthScope
    abstract fun bindLoginActivityViewModel(viewModel: AuthActivityViewModel): ViewModel
}
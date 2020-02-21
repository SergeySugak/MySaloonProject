package com.app.msa_auth.di

import androidx.lifecycle.ViewModel
import com.app.msa_auth.ui.AuthActivityViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthActivityViewModel::class)
    abstract fun bindLoginActivityViewModel(viewModel: AuthActivityViewModel): ViewModel
}
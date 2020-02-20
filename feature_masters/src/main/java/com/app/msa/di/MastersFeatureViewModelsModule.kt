package com.app.msa.di

import androidx.lifecycle.ViewModel
import com.app.msa.masters.MastersViewModel
import com.app.msa.scopes.MastersScope
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MastersFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MastersViewModel::class)
    @MastersScope
    abstract fun bindMastersFragmentViewModel(viewModel: MastersViewModel): ViewModel
}
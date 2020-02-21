package com.app.msa_main.masters.di

import androidx.lifecycle.ViewModel
import com.app.msa_main.masters.MastersViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MastersFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(com.app.msa_main.masters.MastersViewModel::class)
    abstract fun bindMastersFragmentViewModel(viewModel: com.app.msa_main.masters.MastersViewModel): ViewModel
}
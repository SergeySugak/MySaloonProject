package com.app.feature_masters.di

import androidx.lifecycle.ViewModel
import com.app.feature_masters.ui.MastersViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MastersFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MastersViewModel::class)
    abstract fun bindMastersFragmentViewModel(viewModel: MastersViewModel): ViewModel
}
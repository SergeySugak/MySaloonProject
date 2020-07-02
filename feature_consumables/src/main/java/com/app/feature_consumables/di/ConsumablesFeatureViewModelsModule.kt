package com.app.feature_consumables.di

import androidx.lifecycle.ViewModel
import com.app.feature_consumables.ui.ConsumablesViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ConsumablesFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ConsumablesViewModel::class)
    abstract fun bindConsumablesFragmentViewModel(viewModel: ConsumablesViewModel): ViewModel
}
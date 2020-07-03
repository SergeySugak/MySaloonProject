package com.app.feature_consumable.di

import androidx.lifecycle.ViewModel
import com.app.feature_consumable.ui.ConsumableViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ConsumableFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ConsumableViewModel::class)
    abstract fun bindNewConsumableFragmentViewModel(viewModel: ConsumableViewModel): ViewModel
}
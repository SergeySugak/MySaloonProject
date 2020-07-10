package com.app.feature_select_consumables.di

import androidx.lifecycle.ViewModel
import com.app.feature_select_consumables.ui.ConsumablesSelectionDialogViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SelectConsumablesFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ConsumablesSelectionDialogViewModel::class)
    abstract fun bindConsumablesSelectionDialogViewModel(viewModel: ConsumablesSelectionDialogViewModel): ViewModel
}
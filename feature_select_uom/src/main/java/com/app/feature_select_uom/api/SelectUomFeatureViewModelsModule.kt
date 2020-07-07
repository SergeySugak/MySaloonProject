package com.app.feature_select_uom.api

import androidx.lifecycle.ViewModel
import com.app.feature_select_uom.ui.UomSelectionDialogViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SelectUomFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(UomSelectionDialogViewModel::class)
    abstract fun bindUomSelectionDialogViewModel(viewModel: UomSelectionDialogViewModel): ViewModel
}
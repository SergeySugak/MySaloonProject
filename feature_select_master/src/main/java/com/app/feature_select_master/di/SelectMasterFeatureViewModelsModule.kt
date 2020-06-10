package com.app.feature_select_master.di

import androidx.lifecycle.ViewModel
import com.app.feature_select_master.ui.MasterSelectionDialogViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SelectMasterFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MasterSelectionDialogViewModel::class)
    abstract fun bindMasterServicesSelectionDialogViewModel(viewModel: MasterSelectionDialogViewModel): ViewModel
}
package com.app.feature_master.di

import androidx.lifecycle.ViewModel
import com.app.feature_master.ui.MasterServicesSelectionDialog
import com.app.feature_master.ui.MasterServicesSelectionDialogViewModel
import com.app.feature_master.ui.MasterViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MasterFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MasterViewModel::class)
    abstract fun bindNewServiceFragmentViewModel(viewModel: MasterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MasterServicesSelectionDialogViewModel::class)
    abstract fun bindMasterServicesSelectionDialogViewModel(viewModel: MasterServicesSelectionDialogViewModel): ViewModel
}
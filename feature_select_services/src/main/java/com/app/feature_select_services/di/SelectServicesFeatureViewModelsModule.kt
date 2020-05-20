package com.app.feature_select_services.di

import androidx.lifecycle.ViewModel
import com.app.feature_select_services.ui.ServicesSelectionDialogViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SelectServicesFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServicesSelectionDialogViewModel::class)
    abstract fun bindMasterServicesSelectionDialogViewModel(viewModel: ServicesSelectionDialogViewModel): ViewModel
}
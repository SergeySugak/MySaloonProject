package com.app.feature_service_duration.di

import androidx.lifecycle.ViewModel
import com.app.feature_service_duration.ui.ServiceDurationSelectionDialogViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ServiceDurationFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServiceDurationSelectionDialogViewModel::class)
    abstract fun bindServiceDurationSelectionDialogViewModel(viewModel: ServiceDurationSelectionDialogViewModel): ViewModel
}
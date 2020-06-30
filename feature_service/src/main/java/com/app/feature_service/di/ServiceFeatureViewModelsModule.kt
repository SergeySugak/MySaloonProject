package com.app.feature_service.di

import androidx.lifecycle.ViewModel
import com.app.feature_service.ui.ServiceViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ServiceFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServiceViewModel::class)
    abstract fun bindNewServiceFragmentViewModel(viewModel: ServiceViewModel): ViewModel
}
package com.app.feature_services.di

import androidx.lifecycle.ViewModel
import com.app.feature_services.ui.ServicesViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ServicesFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServicesViewModel::class)
    abstract fun bindServicesFragmentViewModel(viewModel: ServicesViewModel): ViewModel
}
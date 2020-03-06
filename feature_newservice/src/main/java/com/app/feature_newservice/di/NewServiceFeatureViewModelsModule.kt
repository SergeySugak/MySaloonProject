package com.app.feature_newservice.di

import androidx.lifecycle.ViewModel
import com.app.feature_newservice.ui.NewServiceViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NewServiceFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(NewServiceViewModel::class)
    abstract fun bindNewServiceFragmentViewModel(viewModel: NewServiceViewModel): ViewModel
}
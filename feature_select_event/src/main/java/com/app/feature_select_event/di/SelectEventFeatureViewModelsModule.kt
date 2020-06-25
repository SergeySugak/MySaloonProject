package com.app.feature_select_event.di

import androidx.lifecycle.ViewModel
import com.app.feature_select_event.ui.EventSelectionDialogViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SelectEventFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventSelectionDialogViewModel::class)
    abstract fun bindEventSelectionDialogViewModel(viewModel: EventSelectionDialogViewModel): ViewModel
}
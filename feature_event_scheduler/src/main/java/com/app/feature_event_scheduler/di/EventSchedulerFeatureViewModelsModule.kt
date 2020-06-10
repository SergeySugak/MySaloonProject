package com.app.feature_event_scheduler.di

import androidx.lifecycle.ViewModel
import com.app.feature_event_scheduler.ui.DateAndTimeSelectionViewModel
import com.app.feature_event_scheduler.ui.EventSchedulerViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EventSchedulerFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventSchedulerViewModel::class)
    abstract fun bindEventSchedulerViewModel(viewModel: EventSchedulerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DateAndTimeSelectionViewModel::class)
    abstract fun bindDateSelectionViewModel(viewModel: DateAndTimeSelectionViewModel): ViewModel
}
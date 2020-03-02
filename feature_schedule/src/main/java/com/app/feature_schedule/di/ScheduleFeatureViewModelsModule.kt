package com.app.feature_schedule.di

import androidx.lifecycle.ViewModel
import com.app.feature_schedule.ui.ScheduleViewModel
import com.app.mscorebase.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ScheduleFeatureViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ScheduleViewModel::class)
    abstract fun bindScheduleFragmentViewModel(viewModel: ScheduleViewModel): ViewModel
}
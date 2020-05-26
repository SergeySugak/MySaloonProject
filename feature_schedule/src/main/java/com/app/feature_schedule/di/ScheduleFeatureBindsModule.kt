package com.app.feature_schedule.di

import com.app.mscorebase.ui.Colorizer
import com.app.mscoremodels.saloon.EventColorizer
import dagger.Binds
import dagger.Module

@Module
abstract class ScheduleFeatureBindsModule {

    @Binds
    abstract fun bindEventColorizer(eventColorizer: EventColorizer): Colorizer
}
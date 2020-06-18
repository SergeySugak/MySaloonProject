package com.app.feature_event_scheduler.di

import com.app.mscorebase.ui.Colorizer
import com.app.mscoremodels.saloon.EventColorizer
import dagger.Binds
import dagger.Module

@Module
abstract class EventSchedulerFeatureBindsModule {
    @Binds
    abstract fun bindEventColorizer(eventColorizer: EventColorizer): Colorizer
}
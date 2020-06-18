package com.app.feature_event_scheduler.di

import com.app.feature_event_scheduler.api.EventSchedulerFeatureDependencies
import com.app.feature_event_scheduler.ui.DateSelectionDialog
import com.app.feature_event_scheduler.ui.EventSchedulerFragment
import com.app.feature_event_scheduler.ui.TimeSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [EventSchedulerFeatureBindsModule::class,
        EventSchedulerFeatureViewModelsModule::class],
    dependencies = [EventSchedulerFeatureDependencies::class]
)
@FeatureScope
interface EventSchedulerFeatureComponent {
    fun inject(fragment: EventSchedulerFragment)
    fun inject(dialog: DateSelectionDialog)
    fun inject(dialog: TimeSelectionDialog)
}
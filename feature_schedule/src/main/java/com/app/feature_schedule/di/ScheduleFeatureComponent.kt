package com.app.feature_schedule.di

import com.app.feature_schedule.api.ScheduleFeatureDependencies
import com.app.feature_schedule.ui.ScheduleFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [ScheduleFeatureViewModelsModule::class],
    dependencies = [ScheduleFeatureDependencies::class])
@FeatureScope
interface ScheduleFeatureComponent {
    fun inject(scheduleFragment: ScheduleFragment)
}
package com.app.msa_main.di

import com.app.feature_masters.api.MastersFeatureDependencies
import com.app.feature_schedule.api.ScheduleFeatureDependencies
import com.app.feature_services.api.ServicesFeatureDependencies
import com.app.mscorebase.di.ComponentDependencies
import com.app.mscorebase.di.ComponentDependenciesKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ComponentDependenciesModule {
    @Binds
    @IntoMap
    @ComponentDependenciesKey(MastersFeatureDependencies::class)
    abstract fun provideMastersFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(ServicesFeatureDependencies::class)
    abstract fun provideServicesFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(ScheduleFeatureDependencies::class)
    abstract fun provideScheduleFeatureDependencies(component: MainFeatureComponent): ComponentDependencies
}
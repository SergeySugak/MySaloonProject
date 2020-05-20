package com.app.msa_main.di

import com.app.feature_event_scheduler.api.EventSchedulerFeatureDependencies
import com.app.feature_master.api.MasterFeatureDependencies
import com.app.feature_masters.api.MastersFeatureDependencies
import com.app.feature_service.api.ServiceFeatureDependencies
import com.app.feature_schedule.api.ScheduleFeatureDependencies
import com.app.feature_select_services.api.SelectServicesFeatureDependencies
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
    @ComponentDependenciesKey(MasterFeatureDependencies::class)
    abstract fun provideMasterFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(ServicesFeatureDependencies::class)
    abstract fun provideServicesFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(ServiceFeatureDependencies::class)
    abstract fun provideServiceFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(ScheduleFeatureDependencies::class)
    abstract fun provideScheduleFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(EventSchedulerFeatureDependencies::class)
    abstract fun provideEventSchedulerFeatureDependencies(component: MainFeatureComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(SelectServicesFeatureDependencies::class)
    abstract fun provideSelectServiceFeatureDependencies(component: MainFeatureComponent): ComponentDependencies
}
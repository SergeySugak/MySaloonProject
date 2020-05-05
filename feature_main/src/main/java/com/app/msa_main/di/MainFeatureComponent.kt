package com.app.msa_main.di

import com.app.feature_event_scheduler.api.EventSchedulerFeatureDependencies
import com.app.feature_master.api.MasterFeatureDependencies
import com.app.feature_masters.api.MastersFeatureDependencies
import com.app.feature_service.api.ServiceFeatureDependencies
import com.app.feature_schedule.api.ScheduleFeatureDependencies
import com.app.feature_services.api.ServicesFeatureDependencies
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_db_repo.repository.di.DbRepositoryModule
import com.app.msa_main.api.MainFeatureDependencies
import com.app.msa_main.main.MainActivity
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.appstate.AppStateManager
import dagger.Component

@Component(modules = [MainFeatureModule::class,
                        MainFeatureViewModelsModule::class,
                        DbRepositoryModule::class,
                        ComponentDependenciesModule::class],
                        dependencies = [MainFeatureDependencies::class])
@FeatureScope
interface MainFeatureComponent: MastersFeatureDependencies,
                                MasterFeatureDependencies,
                                ServicesFeatureDependencies,
                                ScheduleFeatureDependencies,
                                ServiceFeatureDependencies,
                                EventSchedulerFeatureDependencies {
    override fun appStateManager(): AppStateManager
    override fun dbRepository(): DbRepository
    override fun appNavigator(): AppNavigator

    fun inject(mainActivity: MainActivity)
}
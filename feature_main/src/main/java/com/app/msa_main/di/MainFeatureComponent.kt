package com.app.msa_main.di

import com.app.feature_masters.api.MastersFeatureDependencies
import com.app.feature_newservice.api.NewServiceFeatureDependencies
import com.app.feature_newservice.ui.NewServiceFragment
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
                                ServicesFeatureDependencies,
                                ScheduleFeatureDependencies,
                                NewServiceFeatureDependencies {
    override fun appStateManager(): AppStateManager
    override fun dbRepository(): DbRepository
    override fun appNavigator(): AppNavigator

    fun inject(mainActivity: MainActivity)
}
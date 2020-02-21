package com.app.msa_main.di

import com.app.msa_main.api.MainFeatureDependencies
import com.app.msa_main.main.MainActivity
import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.appstate.AppState
import com.app.msa_main.masters.api.MastersFeatureDependencies
import com.app.mscorebase.di.ComponentDependencies
import com.app.mscorebase.di.ComponentDependenciesKey
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@Component(modules = [MainFeatureModule::class,
    MainFeatureViewModelsModule::class,
    ComponentDependenciesModule::class],
    dependencies = [MainFeatureDependencies::class])
@FeatureScope
interface MainFeatureComponent: MastersFeatureDependencies {
    override fun appState(): AppState
    fun inject(mainActivity: MainActivity)
}

@Module
private abstract class ComponentDependenciesModule {
    @Binds
    @IntoMap
    @ComponentDependenciesKey(MastersFeatureDependencies::class)
    abstract fun provideMastersFeatureDependencies(component: MainFeatureComponent): ComponentDependencies
}

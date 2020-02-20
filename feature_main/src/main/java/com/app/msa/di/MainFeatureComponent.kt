package com.app.msa.di

import com.app.msa.api.MainFeatureDependencies
import com.app.msa.api.MastersFeatureDependencies
import com.app.msa.main.MainActivity
import com.app.msa.scopes.MainScope
import com.app.mscorebase.di.ComponentDependencies
import com.app.mscorebase.di.ComponentDependenciesKey
import dagger.Binds
import dagger.Component
import dagger.multibindings.IntoMap

@Component(modules = [MainFeatureModule::class, MainFeatureViewModelsModule::class, ComponentDependenciesModule::class],
    dependencies = [MainFeatureDependencies::class])
@MainScope
interface MainFeatureComponent: MastersFeatureDependencies {
    fun inject(mainActivity: MainActivity)
}

@dagger.Module
private abstract class ComponentDependenciesModule private constructor() {
    @Binds
    @IntoMap
    @ComponentDependenciesKey(MastersFeatureDependencies::class)
    abstract fun provideMastersFeatureDependencies(component: MainFeatureComponent): ComponentDependencies
}

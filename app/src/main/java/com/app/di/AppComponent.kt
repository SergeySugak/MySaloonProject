package com.app.di

import android.content.Context
import com.app.msa.App
import com.app.msa.api.AuthFeatureDependencies
import com.app.msa.api.MainFeatureDependencies
import com.app.msa.navigation.AppNavigator
import com.app.msa.navigation_impl.AppNavigatorImpl
import com.app.mscorebase.di.ComponentDependencies
import com.app.mscorebase.di.ComponentDependenciesKey
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, BindingsModule::class,
                    ComponentDependenciesModule::class])
interface AppComponent: AuthFeatureDependencies, MainFeatureDependencies {

    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}

@dagger.Module
private abstract class BindingsModule {
    @Binds
    @Singleton
    abstract fun appNavigator(appNavigator: AppNavigatorImpl): AppNavigator
}

@dagger.Module
private abstract class ComponentDependenciesModule private constructor() {
    @Binds
    @IntoMap
    @ComponentDependenciesKey(AuthFeatureDependencies::class)
    abstract fun provideAuthDependencies(component: AppComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(MainFeatureDependencies::class)
    abstract fun provideMainFeatureDependencies(component: AppComponent): ComponentDependencies
}

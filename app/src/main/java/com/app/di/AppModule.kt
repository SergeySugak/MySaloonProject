package com.app.di

import android.content.Context
import android.content.SharedPreferences
import com.app.feature_event_scheduler.api.EventSchedulerFeatureDependencies
import com.app.models.AuthManagerImpl
import com.app.msa_auth.api.AuthFeatureDependencies
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_db_repo.repository.db.FirebaseDbRepository
import com.app.msa_main.api.MainFeatureDependencies
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.msa_nav_impl.navigation_impl.AppNavigatorImpl
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateHolder
import com.app.mscorebase.di.ComponentDependencies
import com.app.mscorebase.di.ComponentDependenciesKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import main.java.com.app.mscorebase.auth.AuthManager
import javax.inject.Named
import javax.inject.Singleton

@Module
object AppModule {

    private val APP_SHARED_PREFS = "MSA_STATE_SHARED_PREFS"

    @Provides
    @Singleton
    @Named("STATE")
    internal fun providesSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    internal fun provideGson() = GsonBuilder().create()

    @Provides
    @Singleton
    internal fun provideAppState(
        context: Context,
        authManager: AuthManager,
        @Named("STATE")
        sharedPrefs: SharedPreferences,
        gson: Gson
    ): AppStateManager {
        val appState = AppState(context, authManager, sharedPrefs, gson)
        //Тут добавляем StateManager-ы, которые существуют все время работы приложения
        appState.attachStateHolder(authManager as StateHolder) // authManager = AuthManagerImpl = StateHolder
        return appState
    }
}

@Module
abstract class BindingsModule {
    @Binds
    @Singleton
    abstract fun appNavigator(appNavigator: AppNavigatorImpl): AppNavigator

    @Binds
    @Singleton
    abstract fun dbRepository(repository: FirebaseDbRepository): DbRepository

    @Binds
    @Singleton
    abstract fun bindAuthManager(authManager: AuthManagerImpl): AuthManager
}

@Module
abstract class ComponentDependenciesModule private constructor() {
    @Binds
    @IntoMap
    @ComponentDependenciesKey(AuthFeatureDependencies::class)
    abstract fun provideAuthDependencies(component: AppComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(MainFeatureDependencies::class)
    abstract fun provideMainFeatureDependencies(component: AppComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(EventSchedulerFeatureDependencies::class)
    abstract fun provideEventSchedulerFeatureDependencies(component: AppComponent): ComponentDependencies

}

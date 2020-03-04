package com.app.di

import android.content.Context
import android.content.SharedPreferences
import com.app.models.AuthManagerImpl
import com.app.msa.repository.auth.FirebaseDbRepository
import com.app.msa_auth.api.AuthFeatureDependencies
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_main.api.MainFeatureDependencies
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.msa_nav_impl.navigation_impl.AppNavigatorImpl
import com.app.mscorebase.appstate.AppState
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
import javax.inject.Singleton

@Module
object AppModule {

    private val APP_SHARED_PREFS = "MSA_SHARED_PREFS"

    @Provides
    @Singleton
    internal fun providesSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    internal fun provideGson() = GsonBuilder().create()

    @Provides
    @Singleton
    internal fun provideAppState(authManager: AuthManager, sharedPrefs: SharedPreferences, gson: Gson): AppState {
        val appState = AppState(authManager, sharedPrefs, gson)
        //Тут добавляем StateManager-ы, которые существуют все время работы приложения
        appState.attachStateManager(authManager as StateHolder) // authManager = AuthManagerImpl = StateHolder
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
}

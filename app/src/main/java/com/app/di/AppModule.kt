package com.app.di

import android.content.Context
import android.content.SharedPreferences
import com.app.mscorebase.appstate.AppState
import dagger.Module
import dagger.Provides
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
    internal fun provideAppState(sharedPrefs: SharedPreferences): AppState {
        val appState = AppState(sharedPrefs)
        //Тут добавляем StateManager-ы, которые существуют все время работы приложения
        //например, appState.attachStateManager(authManager)
        return appState
    }

}
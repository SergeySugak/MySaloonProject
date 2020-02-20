package com.app.msa.api

import com.app.msa.navigation.AppNavigator
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.di.ComponentDependencies

interface AuthFeatureDependencies: ComponentDependencies {
    fun appState(): AppState
    fun appNavigator(): AppNavigator
}
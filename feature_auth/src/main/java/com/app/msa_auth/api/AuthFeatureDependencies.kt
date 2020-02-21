package com.app.msa_auth.api

import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.di.ComponentDependencies

interface AuthFeatureDependencies: ComponentDependencies {
    fun appState(): AppState
    fun appNavigator(): AppNavigator
}
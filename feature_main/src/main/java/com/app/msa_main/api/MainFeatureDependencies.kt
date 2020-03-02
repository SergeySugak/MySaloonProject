package com.app.msa_main.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.di.ComponentDependencies

interface MainFeatureDependencies: ComponentDependencies {
    fun appState(): AppState
    fun appNavigator(): AppNavigator
    fun dbRepository(): DbRepository
}
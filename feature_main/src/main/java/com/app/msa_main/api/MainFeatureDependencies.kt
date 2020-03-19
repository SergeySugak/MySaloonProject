package com.app.msa_main.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.di.ComponentDependencies

interface MainFeatureDependencies: ComponentDependencies {
    fun appStateManager(): AppStateManager
    fun appNavigator(): AppNavigator
    fun dbRepository(): DbRepository
}
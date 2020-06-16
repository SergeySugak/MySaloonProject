package com.app.feature_masters.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.di.ComponentDependencies

interface MastersFeatureDependencies : ComponentDependencies {
    fun appStateManager(): AppStateManager
    fun dbRepository(): DbRepository
    fun appNavigator(): AppNavigator
}
package com.app.msa_auth.api

import com.app.msa_auth_repo.repository.auth.AuthRepository
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.di.ComponentDependencies

interface AuthFeatureDependencies : ComponentDependencies {
    fun appStateManager(): AppStateManager
    fun appNavigator(): AppNavigator
    fun dbRepository(): DbRepository
    fun authRepository(): AuthRepository
}
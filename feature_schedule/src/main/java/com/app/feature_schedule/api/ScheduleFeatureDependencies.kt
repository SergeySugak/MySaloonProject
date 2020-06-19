package com.app.feature_schedule.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.di.ComponentDependencies
import com.google.gson.Gson

interface ScheduleFeatureDependencies : ComponentDependencies {
    fun appStateManager(): AppStateManager
    fun dbRepository(): DbRepository
    fun appNavigator(): AppNavigator
    fun gson(): Gson
}
package com.app.feature_service_duration.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.di.ComponentDependencies

interface ServiceDurationFeatureDependencies : ComponentDependencies {
    fun appStateManager(): AppStateManager
    fun dbRepository(): DbRepository
}
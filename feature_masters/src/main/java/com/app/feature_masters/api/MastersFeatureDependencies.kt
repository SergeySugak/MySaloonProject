package com.app.feature_masters.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.di.ComponentDependencies

interface MastersFeatureDependencies: ComponentDependencies {
    fun appState(): AppState
    fun dbRepository(): DbRepository
}
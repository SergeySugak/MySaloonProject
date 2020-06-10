package com.app.feature_select_master.api

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.di.ComponentDependencies

interface SelectMasterFeatureDependencies : ComponentDependencies {
    fun appStateManager(): AppStateManager
    fun dbRepository(): DbRepository
}
package com.app.msa.api

import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.di.ComponentDependencies

interface MastersFeatureDependencies: ComponentDependencies {
    fun appState(): AppState
}
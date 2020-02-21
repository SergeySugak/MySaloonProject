package com.app.msa_main.api

import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.di.ComponentDependencies

interface MainFeatureDependencies: ComponentDependencies {
    fun appState(): AppState
}
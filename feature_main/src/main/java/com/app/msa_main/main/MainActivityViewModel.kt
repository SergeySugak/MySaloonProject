package com.app.msa_main.main

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSActivityViewModel
import javax.inject.Inject

class MainActivityViewModel
@Inject constructor(appState: AppStateManager) : MSActivityViewModel(appState) {

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }
}
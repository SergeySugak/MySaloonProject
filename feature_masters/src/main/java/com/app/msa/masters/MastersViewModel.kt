package com.app.msa.masters

import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSFragmentViewModel
import javax.inject.Inject

class MastersViewModel @Inject constructor(appState: AppState) : MSFragmentViewModel(appState) {

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }
}
